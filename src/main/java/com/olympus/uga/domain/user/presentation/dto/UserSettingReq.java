package com.olympus.uga.domain.user.presentation.dto;

import com.olympus.uga.domain.user.domain.enums.Gender;
import com.olympus.uga.domain.user.domain.enums.UserCharacter;

public record UserSettingReq(String username,
                             String birth,
                             Gender gender,
                             String mbti,
                             UserCharacter character,
                             String interests) {
}
