package com.olympus.uga.domain.user.presentation.dto;

import com.olympus.uga.domain.user.domain.enums.Gender;

public record UsernameBirthGenderReq(String username,
                                     String birth,
                                     Gender gender) {
}
