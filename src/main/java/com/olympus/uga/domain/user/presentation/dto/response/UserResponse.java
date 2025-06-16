package com.olympus.uga.domain.user.presentation.dto.response;

import com.olympus.uga.domain.user.domain.User;
import lombok.Builder;

@Builder
public record UserResponse(String username, String profileImage) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .profileImage(user.getProfileImage())
                .build();
    }
}
