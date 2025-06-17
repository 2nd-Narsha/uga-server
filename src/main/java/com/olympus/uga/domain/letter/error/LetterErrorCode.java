package com.olympus.uga.domain.letter.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LetterErrorCode implements CustomErrorCode {
    LETTER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 편지를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
