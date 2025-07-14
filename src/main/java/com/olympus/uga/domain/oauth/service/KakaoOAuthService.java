package com.olympus.uga.domain.oauth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olympus.uga.domain.oauth.error.OAuthErrorCode;
import com.olympus.uga.domain.oauth.presentation.dto.response.KakaoUserInfoDto;
import com.olympus.uga.domain.oauth.presentation.dto.response.KakaoUserResponseDto;
import com.olympus.uga.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String KAKAO_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    public KakaoUserInfoDto getUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URI,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            KakaoUserResponseDto userResponse = objectMapper.readValue(response.getBody(), KakaoUserResponseDto.class);

            return new KakaoUserInfoDto(
                    String.valueOf(userResponse.getId()),
                    userResponse.getKakaoAccount().getEmail()
            );
        } catch (Exception e) {
            throw new CustomException(OAuthErrorCode.KAKAO_API_ERROR);
        }
    }
}