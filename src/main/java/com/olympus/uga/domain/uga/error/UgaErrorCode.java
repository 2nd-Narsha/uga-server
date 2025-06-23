package com.olympus.uga.domain.uga.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UgaErrorCode implements CustomErrorCode {
    UGA_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 우가를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
