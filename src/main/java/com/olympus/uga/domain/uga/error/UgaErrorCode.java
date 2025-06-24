package com.olympus.uga.domain.uga.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UgaErrorCode implements CustomErrorCode {
    UGA_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 우가를 찾을 수 없습니다."),
    NOT_FAMILY_UGA(HttpStatus.FORBIDDEN, "가족의 우가가 아닙니다."),
    UGA_FULLY_GROWN(HttpStatus.BAD_REQUEST, "이미 다 자란 우가입니다. 먹이를 줄 수 없습니다."),
    UGA_ALREADY_INDEPENDENCE(HttpStatus.BAD_REQUEST, "이미 독립한 우가입니다."),
    INVALID_FOOD_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 먹이 타입입니다."),
    UGA_NOT_FULLY_GROWN(HttpStatus.BAD_REQUEST, "아직 다 자라지 않은 우가입니다.");

    private final HttpStatus status;
    private final String message;
}
