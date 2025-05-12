package com.olympus.uga.domain.sms.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SmsErrorCode implements CustomErrorCode {
    PHONE_NUM_CONFLICT(HttpStatus.CONFLICT, "해당 전화번호가 이미 존재합니다."),
    INVALID_CODE(HttpStatus.BAD_REQUEST,"인증번호가 유효하지 않습니다."),
    PHONE_NUM_NOT_FOUND(HttpStatus.NOT_FOUND,"전화번호가 존재하지 않습니다."),
    PHONE_NUM_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "인증되지 않은 전화번호입니다."),
    PHONE_NUM_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "이미 인증된 전화번호입니다.");

    private final HttpStatus status;
    private final String message;
}
