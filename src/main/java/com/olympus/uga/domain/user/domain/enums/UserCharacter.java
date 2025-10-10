package com.olympus.uga.domain.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserCharacter {
    SOBORO("https://ugafinal.s3.ap-northeast-2.amazonaws.com/%E1%84%80%E1%85%A9%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%8B%E1%85%B5.png"),
    CHUNBOGI("https://ugafinal.s3.ap-northeast-2.amazonaws.com/%E1%84%8E%E1%85%AE%E1%86%AB%E1%84%87%E1%85%A9%E1%86%A8%E1%84%8B%E1%85%B5.png"),
    BERINGKEON("https://ik.imagekit.io/yoonha2017/beringkeonProfile.png?updatedAt=1760087286015 \n"),
    BERRY("https://ik.imagekit.io/yoonha2017/berryProfile.png?updatedAt=1760087299596"),
    CHARLES("https://ik.imagekit.io/yoonha2017/charseProfile.png?updatedAt=1760087300067"),
    BONGGU("https://ugafinal.s3.ap-northeast-2.amazonaws.com/%E1%84%80%E1%85%A9%E1%86%B7.png"),
    NICHOLAS("https://ugafinal.s3.ap-northeast-2.amazonaws.com/%E1%84%82%E1%85%B5%E1%84%8F%E1%85%A9%E1%86%AF%E1%84%85%E1%85%A1%E1%84%89%E1%85%B3.png"),
    MILK("https://ik.imagekit.io/yoonha2017/milkProfile.png?updatedAt=1760087335955"),
    HODU("https://ik.imagekit.io/yoonha2017/hoduProfile.png?updatedAt=1760087323741"),
    NAELLEUM(""),
    OGONGI("https://ik.imagekit.io/yoonha2017/ogongeProfile.png?updatedAt=1760087335839"),
    PENGDUGI("https://ik.imagekit.io/yoonha2017/paengddugiProfile.png?updatedAt=1760087308242"),
    ACHIMI("https://ik.imagekit.io/yoonha2017/achimiProfile.png?updatedAt=1760087323526");

    private final String imageUrl;
}
