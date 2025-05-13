package com.olympus.uga.domain.auth.presentation.dto.request;

import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.enums.UserCharacter;
import com.olympus.uga.domain.user.domain.enums.Gender;

public record SignUpReq(
        String phoneNum,
        String password,
        String username,
        String birth,
        Gender gender,
        String mbti,
        UserCharacter character,
        String interests) {
    public static User fromSignUpReq(SignUpReq req, String password) {
        return User.builder()
                .phoneNum(req.phoneNum)
                .password(password)
                .username(req.username)
                .birth(req.birth)
                .gender(req.gender)
                .mbti(req.mbti)
                .character(req.character)
                .interests(req.interests)
                .build();
    }
}
