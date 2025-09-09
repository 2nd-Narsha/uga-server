package com.olympus.uga.domain.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserCharacter {
    SOBORO("https://ugafinal.s3.ap-northeast-2.amazonaws.com/%E1%84%80%E1%85%A9%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%8B%E1%85%B5.png"),
    CHUNBOGI("https://ugafinal.s3.ap-northeast-2.amazonaws.com/%E1%84%8E%E1%85%AE%E1%86%AB%E1%84%87%E1%85%A9%E1%86%A8%E1%84%8B%E1%85%B5.png"),
    BERINGKEON(""),
    BERRY(""),
    CHARLES(""),
    BONGGU("https://ugafinal.s3.ap-northeast-2.amazonaws.com/%E1%84%80%E1%85%A9%E1%86%B7.png"),
    NICHOLAS("https://ugafinal.s3.ap-northeast-2.amazonaws.com/%E1%84%82%E1%85%B5%E1%84%8F%E1%85%A9%E1%86%AF%E1%84%85%E1%85%A1%E1%84%89%E1%85%B3.png"),
    MILK(""),
    HODU(""),
    NAELLEUM(""),
    OGONGI(""),
    PENGDUGI(""),
    ACHIMI("");

    private final String imageUrl;
}
