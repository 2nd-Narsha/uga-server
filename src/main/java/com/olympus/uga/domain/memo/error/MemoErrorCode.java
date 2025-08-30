package com.olympus.uga.domain.memo.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemoErrorCode implements CustomErrorCode {

    MEMO_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자의 메모가 존재하지 않습니다."),
    MEMO_EXPIRED(HttpStatus.GONE, "하루 이내의 메모가 없습니다."),
    INVALID_LOCATION(HttpStatus.BAD_REQUEST, "위치 정보가 올바르지 않습니다.");

    private final HttpStatus status;
    private final String message;
}
