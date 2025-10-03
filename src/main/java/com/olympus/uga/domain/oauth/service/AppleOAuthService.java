package com.olympus.uga.domain.oauth.service;

import com.olympus.uga.domain.oauth.presentation.dto.response.ApplePublicKeyDto;
import com.olympus.uga.domain.oauth.presentation.dto.response.ApplePublicKeysDto;
import com.olympus.uga.domain.oauth.presentation.dto.response.AppleTokenResponseDto;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.enums.LoginType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleOAuthService {

    @Value("${oauth.apple.team-id}")
    private String teamId;

    @Value("${oauth.apple.client-id}")
    private String clientId;

    @Value("${oauth.apple.key-id}")
    private String keyId;

    @Value("${oauth.apple.private-key}")
    private String privateKey;

    private static final long THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000;

    /**
     * 애플 로그인 처리
     */
    public User loginWithApple(String code) {
        // 1. code로 애플 토큰 받기
        AppleTokenResponseDto appleToken = getAppleToken(code);

        // 2. id_token에서 유저 정보 추출 및 검증
        return extractUserFromIdToken(appleToken.getIdToken());
    }

    /**
     * 애플 API에 토큰 요청
     */
    private AppleTokenResponseDto getAppleToken(String code) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://appleid.apple.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
                .build();

        try {
            log.info("[애플 로그인] 토큰 요청 시작");
            log.info("[애플 로그인] code: {}", code);
            log.info("[애플 로그인] clientId: {}", clientId);

            String clientSecret = makeClientSecretToken();
            log.info("[애플 로그인] clientSecret 생성 완료");

            return webClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/auth/token")
                            .queryParam("grant_type", "authorization_code")
                            .queryParam("client_id", clientId)
                            .queryParam("client_secret", clientSecret)
                            .queryParam("code", code)
                            .build())
                    .retrieve()
                    .bodyToMono(AppleTokenResponseDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("[애플 로그인 실패] 상태코드: {}", e.getStatusCode());
            log.error("[애플 로그인 실패] 응답 본문: {}", e.getResponseBodyAsString());
            log.error("[애플 로그인 실패] 헤더: {}", e.getHeaders());
            throw new RuntimeException("애플 로그인 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("[애플 로그인] 예상치 못한 에러", e);
            throw new RuntimeException("애플 로그인 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * Client Secret JWT 토큰 생성
     */
    private String makeClientSecretToken() {
        log.info("[애플 로그인] teamId: {}", teamId);
        log.info("[애플 로그인] clientId: {}", clientId);
        log.info("[애플 로그인] keyId: {}", keyId);
        log.info("[애플 로그인] privateKey length: {}", privateKey != null ? privateKey.length() : "null");

        try {
            long now = System.currentTimeMillis();

            String token = Jwts.builder()
                    .subject(clientId)
                    .issuer(teamId)
                    .issuedAt(new Date(now))
                    .expiration(new Date(now + THIRTY_DAYS_MS))
                    .audience()
                    .add("https://appleid.apple.com")
                    .and()
                    .header()
                    .keyId(keyId)
                    .add("alg", "ES256")
                    .and()
                    .signWith(getPrivateKey(), Jwts.SIG.ES256)
                    .compact();

            log.info("[애플 로그인] 클라이언트 시크릿 토큰 생성 완료");
            return token;
        } catch (Exception e) {
            log.error("[애플 로그인] 토큰 생성 실패", e);
            throw new RuntimeException("애플 client_secret 토큰 생성 실패: " + e.getMessage());
        }
    }

    /**
     * Private Key 생성
     */
    private PrivateKey getPrivateKey() {
        try {
            // 공백, 줄바꿈 제거
            String cleanedKey = privateKey.replaceAll("\\s+", "");

            byte[] privateKeyBytes = Base64.getDecoder().decode(cleanedKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error("[애플 로그인] Private Key 생성 실패", e);
            throw new RuntimeException("애플 Private Key 생성 실패: " + e.getMessage());
        }
    }

    /**
     * id_token에서 유저 정보 추출 및 검증
     */
    private User extractUserFromIdToken(String idToken) {
        // 애플 공개키 가져오기
        List<ApplePublicKeyDto> publicKeys = getApplePublicKeys();

        // JWT 파싱 및 검증
        AppleKeyLocator keyLocator = new AppleKeyLocator(publicKeys);

        Claims claims = Jwts.parser()
                .keyLocator(keyLocator)
                .build()
                .parseSignedClaims(idToken)
                .getPayload();

        log.info("[애플 로그인] id_token 검증 완료: {}", claims.toString());

        String email = claims.get("email", String.class);
        String oauthId = claims.getSubject();

        // 이메일이 없는 경우 처리
        if (email == null) {
            log.warn("[애플 로그인] 이메일 정보 없음 - oauthId로만 식별: {}", oauthId);
            // oauthId 기반으로 더미 이메일 생성 (선택사항)
            // email = oauthId + "@apple.privaterelay.appleid.com";
        }

        return User.builder()
                .email(email)  // null일 수 있음
                .oauthId(oauthId)
                .loginType(LoginType.APPLE)
                .point(0)
                .isCheckedMailbox(true)
                .build();
    }

    /**
     * 애플 공개키 목록 가져오기
     */
    private List<ApplePublicKeyDto> getApplePublicKeys() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://appleid.apple.com")
                .build();

        try {
            ApplePublicKeysDto response = webClient.get()
                    .uri("/auth/keys")
                    .retrieve()
                    .bodyToMono(ApplePublicKeysDto.class)
                    .block();

            return response != null ? response.getKeys() : List.of();
        } catch (Exception e) {
            log.error("[애플 로그인] 공개키 조회 실패", e);
            throw new RuntimeException("애플 공개키 조회 실패");
        }
    }
}