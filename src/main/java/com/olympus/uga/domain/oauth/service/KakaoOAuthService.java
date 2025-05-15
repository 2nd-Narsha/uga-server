package com.olympus.uga.domain.oauth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olympus.uga.domain.oauth.error.OAuthErrorCode;
import com.olympus.uga.domain.oauth.presentation.dto.response.KakaoUserInfoDto;
import com.olympus.uga.domain.oauth.presentation.dto.response.KakaoUserResponseDto;
import com.olympus.uga.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${oauth.kakao.client-secret}")
    private String clientSecret;

    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;

    private static final String KAKAO_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    public String getAccessToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("redirect_uri", redirectUri);
            body.add("code", code);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new CustomException(OAuthErrorCode.KAKAO_API_ERROR);
        }
    }

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
