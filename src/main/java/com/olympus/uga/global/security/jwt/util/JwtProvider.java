package com.olympus.uga.global.security.jwt.util;

import com.olympus.uga.domain.auth.presentation.dto.response.RefreshRes;
import com.olympus.uga.domain.auth.presentation.dto.response.SignInRes;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.security.jwt.JwtProperties;
import com.olympus.uga.global.security.jwt.enums.TokenType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProperties jwtProperties;
    private final UserJpaRepo userJpaRepo;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public SignInRes createToken(Long userId) {
        return SignInRes.builder()
                .accessToken(createAccessToken(userId))
                .refreshToken(createRefreshToken(userId))
                .build();
    }

    public String createAccessToken(Long userId) {
        return Jwts.builder()
                .header().add("typ", TokenType.ACCESS.toString()).and()
                .subject(String.valueOf(userId))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessExp()))
                .signWith(getSigningKey())
                .compact();
    }

    private String createRefreshToken(Long userId) {
        return Jwts.builder()
                .header().add("typ", TokenType.REFRESH.toString()).and()
                .subject(String.valueOf(userId))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExp()))
                .signWith(getSigningKey())
                .compact();
    }

    public RefreshRes refreshToken(Long userId) {
        return RefreshRes.builder()
                .accessToken(createAccessToken(userId)).build();
    }
}
