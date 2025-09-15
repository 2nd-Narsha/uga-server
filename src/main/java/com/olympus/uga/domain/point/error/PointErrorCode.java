package com.olympus.uga.domain.point.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PointErrorCode implements CustomErrorCode {
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "포인트가 부족합니다."),
    DUPLICATE_PURCHASE(HttpStatus.BAD_REQUEST, "이미 처리된 구매입니다."),
    POINT_REWARD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "포인트 지급 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
