package com.olympus.uga.domain.auth.presentation.dto.request;

import com.olympus.uga.domain.user.domain.User;

import java.util.ArrayList;

public record SignUpReq(
        String phoneNum,
        String password) {
    public static User fromSignUpReq(SignUpReq req, String password) {
        return User.builder()
                .phoneNum(req.phoneNum)
                .password(password)
                .contribution(0)
                .foods(new ArrayList<>())
                .build();
    }
}
