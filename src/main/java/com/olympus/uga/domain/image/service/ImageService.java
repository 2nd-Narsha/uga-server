package com.olympus.uga.domain.image.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.olympus.uga.domain.image.domain.ImageDetails;
import com.olympus.uga.domain.image.domain.repo.ImageRepo;
import com.olympus.uga.domain.image.error.ImageErrorCode;
import com.olympus.uga.domain.image.presentation.dto.dto.ImageInfo;
import com.olympus.uga.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final AmazonS3Client amazonS3Client;
    private final ImageRepo imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    //이미지 업로드(DB, S3 둘 다)
    public ImageInfo uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException(ImageErrorCode.FILE_EMPTY);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String fileUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3Client.putObject(bucket, fileName, inputStream, metadata);

            imageRepository.save(new ImageDetails(fileUrl, fileName));

            return new ImageInfo(fileUrl, fileName);
        } catch (IOException e) {
            throw new CustomException(ImageErrorCode.FILE_CONVERT_FAILED);
        } catch (SdkClientException e) {
            throw new CustomException(ImageErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    //이미지 삭제
    public void deleteImage(String fileName) {
        try {
            boolean isExist = amazonS3Client.doesObjectExist(bucket, fileName);
            if (!isExist) {
                throw new CustomException(ImageErrorCode.FILE_NOT_FOUND);
            }

            amazonS3Client.deleteObject(bucket, fileName);
        } catch (SdkClientException e) {
            throw new CustomException(ImageErrorCode.FILE_DELETE_FAILED);
        }

        imageRepository.delete(imageRepository.findByFileName(fileName).orElseThrow(() -> new CustomException(ImageErrorCode.FILE_NOT_FOUND)));
    }
}