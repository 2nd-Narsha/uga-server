package com.olympus.uga.domain.user.presentation.dto.request;

import com.olympus.uga.domain.user.domain.enums.Gender;

public record UsernameBirthGenderReq(String username,
                                     String birth,
                                     Gender gender) {
}
