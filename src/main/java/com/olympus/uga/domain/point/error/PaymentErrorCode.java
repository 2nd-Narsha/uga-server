package com.olympus.uga.domain.point.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements CustomErrorCode {
    UNSUPPORTED_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "지원하지 않는 결제 방법입니다."),
    PAYMENT_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "결제 검증에 실패했습니다."),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "이미 처리된 결제입니다.");

    private final HttpStatus status;
    private final String message;
}
