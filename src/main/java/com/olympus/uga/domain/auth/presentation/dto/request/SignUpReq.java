package com.olympus.uga.domain.auth.presentation.dto.request;

import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.enums.LoginType;

public record SignUpReq(
        String phoneNum,
        String password) {
    public static User fromSignUpReq(SignUpReq req, String password) {
        return User.builder()
                .phoneNum(req.phoneNum)
                .password(password)
                .loginType(LoginType.LOCAL)
                .build();
    }
}
