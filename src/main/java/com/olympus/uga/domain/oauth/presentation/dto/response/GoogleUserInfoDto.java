package com.olympus.uga.domain.oauth.presentation.dto.response;

import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.enums.LoginType;

public record GoogleUserInfoDto(String id, String email) {
    public static User registerGoogleUser(GoogleUserInfoDto userInfo) {
        return User.builder()
                .email(userInfo.email)
                .loginType(LoginType.GOOGLE)
                .oauthId(userInfo.id)
                .build();
    }
}
