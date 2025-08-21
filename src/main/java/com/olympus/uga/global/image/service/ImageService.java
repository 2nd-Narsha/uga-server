package com.olympus.uga.global.image.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.image.domain.ImageDetails;
import com.olympus.uga.global.image.domain.repo.ImageRepo;
import com.olympus.uga.global.image.error.ImageErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final AmazonS3Client amazonS3Client;
    private final ImageRepo imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");

    // 이미지 업로드
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ImageErrorCode.FILE_EMPTY);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new CustomException(ImageErrorCode.FILE_NAME_INVALID);
        }

        // 확장자 검증
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new CustomException(ImageErrorCode.FILE_EXTENSION_NOT_ALLOWED);
        }

        // 파일 크기 검증 추가
        final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
        final long MIN_FILE_SIZE = 10 * 1024;        // 10KB

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CustomException(ImageErrorCode.FILE_TOO_LARGE);
        }
        if (file.getSize() < MIN_FILE_SIZE) {
            throw new CustomException(ImageErrorCode.FILE_TOO_SMALL);
        }

        String fileName = UUID.randomUUID() + "_" + originalFilename;
        String fileUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            String contentType = file.getContentType();
            if (contentType == null || contentType.isBlank()) {
                throw new CustomException(ImageErrorCode.CONTENT_TYPE_MISSING);
            }

            metadata.setContentType(contentType);
            metadata.setContentLength(file.getSize());

            amazonS3Client.putObject(bucket, fileName, inputStream, metadata);
            imageRepository.save(new ImageDetails(fileUrl, fileName));

            return fileUrl;

        } catch (IOException e) {
            throw new CustomException(ImageErrorCode.FILE_CONVERT_FAILED);
        } catch (AmazonServiceException e) {
            throw new CustomException(ImageErrorCode.AWS_SERVICE_ERROR);
        } catch (SdkClientException e) {
            throw new CustomException(ImageErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    // 이미지 삭제
    @Transactional
    public void deleteImage(String fileName) {
        ImageDetails image = imageRepository.findByImageName(fileName)
                .orElseThrow(() -> new CustomException(ImageErrorCode.FILE_NOT_FOUND));

        imageRepository.delete(image); // DB 먼저 삭제

        try {
            boolean isExist = amazonS3Client.doesObjectExist(bucket, fileName);
            if (!isExist) {
                throw new CustomException(ImageErrorCode.FILE_NOT_FOUND);
            }

            amazonS3Client.deleteObject(bucket, fileName);
        } catch (AmazonServiceException e) {
            throw new CustomException(ImageErrorCode.AWS_SERVICE_ERROR);
        } catch (SdkClientException e) {
            throw new CustomException(ImageErrorCode.FILE_DELETE_FAILED);
        }
    }

    private String getFileExtension(String filename) {
        int index = filename.lastIndexOf(".");
        if (index == -1 || index == filename.length() - 1) {
            throw new CustomException(ImageErrorCode.FILE_EXTENSION_NOT_FOUND);
        }
        return filename.substring(index + 1);
    }
}
