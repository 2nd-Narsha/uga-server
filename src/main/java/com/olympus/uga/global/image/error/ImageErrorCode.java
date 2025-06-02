package com.olympus.uga.global.image.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements CustomErrorCode {
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "업로드한 파일이 비어 있습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이름의 이미지를 찾을 수 없습니다."),
    FILE_CONVERT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 변환 중 문제가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
