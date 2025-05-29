package com.olympus.uga.domain.oauth.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OAuthErrorCode implements CustomErrorCode {
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 OAuth 토큰입니다."),
    USER_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "OAuth 사용자 정보를 찾을 수 없습니다."),
    KAKAO_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API 호출 중 오류가 발생했습니다."),
    GOOGLE_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "구글 API 호출 중 오류가 발생했습니다.");


    private final HttpStatus status;
    private final String message;
}
