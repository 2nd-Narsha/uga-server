package com.olympus.uga.domain.auth.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import com.olympus.uga.global.exception.error.CustomErrorCode;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements CustomErrorCode {
    PHONE_NUM_ALREADY(HttpStatus.CONFLICT, "이미 존재하는 전화번호입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 맞지 않습니다.");

    private final HttpStatus status;
    private final String message;
}
