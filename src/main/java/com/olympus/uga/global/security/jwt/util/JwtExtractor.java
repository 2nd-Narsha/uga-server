package com.olympus.uga.global.security.jwt.util;

import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.error.UserErrorCode;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.AuthDetails;
import com.olympus.uga.global.security.jwt.JwtProperties;
import com.olympus.uga.global.security.jwt.error.JwtErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class JwtExtractor {
    private final JwtProperties jwtProperties;
    private final UserJpaRepo userJpaRepo;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        Jws<Claims> claims = getClaims(token);
        String phoneNum = claims.getBody().getSubject();

        User user = userJpaRepo.findById(phoneNum)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        AuthDetails details = new AuthDetails(user);

        return new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
    }

    private Jws<Claims> getClaims(String token) {
        try{
            return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(JwtErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(JwtErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(JwtErrorCode.INVALID_TOKEN);
        } catch (MalformedJwtException e) {
            throw new CustomException(JwtErrorCode.MALFORMED_TOKEN);
        }
    }
}
