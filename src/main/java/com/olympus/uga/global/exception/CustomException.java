package com.olympus.uga.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.olympus.uga.global.exception.error.CustomErrorCode;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final CustomErrorCode error;
}
