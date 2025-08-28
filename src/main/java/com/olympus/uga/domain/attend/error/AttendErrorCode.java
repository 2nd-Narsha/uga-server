package com.olympus.uga.domain.attend.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AttendErrorCode implements CustomErrorCode {
    ALREADY_CHECKED_TODAY(HttpStatus.BAD_REQUEST, "오늘은 이미 출석체크를 완료했습니다.");

    private final HttpStatus status;
    private final String message;
}