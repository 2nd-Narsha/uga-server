package com.olympus.uga.domain.oauth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olympus.uga.domain.oauth.error.OAuthErrorCode;
import com.olympus.uga.domain.oauth.presentation.dto.response.GoogleUserInfoDto;
import com.olympus.uga.domain.oauth.presentation.dto.response.GoogleUserResponseDto;
import com.olympus.uga.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.client-secret}")
    private String clientSecret;

    private static final String GOOGLE_USER_INFO_URI = "https://www.googleapis.com/oauth2/v3/userinfo";

    public GoogleUserInfoDto getUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    GOOGLE_USER_INFO_URI,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            GoogleUserResponseDto userResponse = objectMapper.readValue(response.getBody(), GoogleUserResponseDto.class);

            return new GoogleUserInfoDto(
                    userResponse.getSub(),
                    userResponse.getEmail()
            );
        } catch (Exception e) {
            throw new CustomException(OAuthErrorCode.GOOGLE_API_ERROR);
        }
    }
}

