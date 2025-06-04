package com.olympus.uga.domain.answer.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AnswerErrorCode implements CustomErrorCode {
    ALREADY_ANSWER(HttpStatus.BAD_REQUEST, "이미 답변을 하였습니다.");

    private final HttpStatus status;
    private final String message;
}
