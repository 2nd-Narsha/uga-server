package com.olympus.uga.domain.oauth.service;

import com.olympus.uga.domain.oauth.presentation.dto.response.ApplePublicKeyDto;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.LocatorAdapter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
public class AppleKeyLocator extends LocatorAdapter<Key> {

    private final List<ApplePublicKeyDto> publicKeyList;

    public AppleKeyLocator(List<ApplePublicKeyDto> publicKeyList) {
        this.publicKeyList = publicKeyList;
    }

    @Override
    protected Key locate(JwsHeader header) {
        log.debug("[애플 로그인] JWT Header: {}", header);

        // kid가 일치하는 공개키 찾기
        Optional<ApplePublicKeyDto> optionalPublicKey = publicKeyList.stream()
                .filter(applePublicKey -> applePublicKey.getKid().equals(header.getKeyId()))
                .findFirst();

        if (optionalPublicKey.isEmpty()) {
            throw new RuntimeException(
                    "일치하는 Apple Public Key가 없습니다. kid: " + header.getKeyId());
        }

        ApplePublicKeyDto publicKey = optionalPublicKey.get();

        // RSA 공개키 생성
        BigInteger n = new BigInteger(1, Base64.getUrlDecoder().decode(publicKey.getN()));
        BigInteger e = new BigInteger(1, Base64.getUrlDecoder().decode(publicKey.getE()));

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n, e);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception error) {
            log.error("[애플 로그인] Public Key 생성 실패", error);
            throw new RuntimeException("애플 Public Key 생성 실패: " + error.getMessage());
        }
    }
}