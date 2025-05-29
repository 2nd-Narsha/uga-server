package com.olympus.uga.domain.uga.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UgaErrorCode implements CustomErrorCode {
    UGA_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이디의 우가를 찾을 수 없습니다."),
    FOOD_SHORTAGE(HttpStatus.BAD_REQUEST, "먹이의 수량이 부족합니다."),
    INVALID_FOOD_TYPE(HttpStatus.BAD_REQUEST, "존재하지 않는 먹이입니다.");

    private final HttpStatus status;
    private final String message;
}
