package com.olympus.uga.domain.family.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FamilyErrorCode implements CustomErrorCode {
    FAMILY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 코드의 가족을 찾을 수 없습니다."),
    NOT_FAMILY_MEMBER(HttpStatus.BAD_REQUEST, "해당 가족의 멤버가 아닙니다."),
    NOT_FAMILY_UGA(HttpStatus.BAD_REQUEST, "해당 가족의 우가가 아닙니다.");
    NOT_FAMILY_MEMBER(HttpStatus.BAD_REQUEST, "해당 가족의 멤버가 아닙니다.");

    private final HttpStatus status;
    private final String message;
}
