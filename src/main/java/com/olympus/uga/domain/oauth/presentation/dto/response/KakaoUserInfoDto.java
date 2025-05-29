package com.olympus.uga.domain.oauth.presentation.dto.response;

import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.enums.LoginType;

public record KakaoUserInfoDto(String id, String email) {
    public static User registerKakaoUser(KakaoUserInfoDto userInfo) {
        return User.builder()
                .email(userInfo.email)
                .loginType(LoginType.KAKAO)
                .oauthId(userInfo.id)
                .build();
    }
}
