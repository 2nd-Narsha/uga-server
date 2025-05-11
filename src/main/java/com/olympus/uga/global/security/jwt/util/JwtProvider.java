package com.olympus.uga.global.security.jwt.util;

import com.olympus.uga.domain.auth.presentation.dto.response.SignInRes;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.security.jwt.JwtProperties;
import com.olympus.uga.global.security.jwt.enums.TokenType;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

    public SignInRes createToken(String phoneNum) {
        return SignInRes.builder()
                .accessToken(createAccessToken(phoneNum))
                .refreshToken(createRefreshToken(phoneNum))
                .build();
    }

    private String createAccessToken(String phoneNum) {
        return Jwts.builder()
                .setHeaderParam(Header.JWT_TYPE, TokenType.ACCESS)
                .setSubject(phoneNum)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessExp()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String createRefreshToken(String phoneNum) {
        return Jwts.builder()
                .setHeaderParam(Header.JWT_TYPE, TokenType.REFRESH)
                .setSubject(phoneNum)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExp()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
