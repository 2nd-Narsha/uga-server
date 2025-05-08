package com.olympus.uga.domain.auth.presentation.dto.request;

import com.olympus.uga.domain.user.domain.enums.Gender;

public record SignUpReq(
        String phoneNum,
        String password,
        String username,
        String birth,
        Gender gender,
        String mbti,
        Character character,
        String interests) {
}
