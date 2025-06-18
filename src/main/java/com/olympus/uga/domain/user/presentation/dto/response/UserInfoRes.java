package com.olympus.uga.domain.user.presentation.dto.response;

import com.olympus.uga.domain.user.domain.User;
import lombok.Builder;

@Builder
public record UserInfoRes(String username, String profileImage, boolean isLeader) {
    // 리더 여부를 직접 지정하는 팩토리 메서드
    public static UserInfoRes from(User user, boolean isLeader) {
        return UserInfoRes.builder()
                .username(user.getUsername())
                .profileImage(user.getProfileImage())
                .isLeader(isLeader)
                .build();
    }

    // 기본값으로 리더가 아닌 것으로 설정하는 팩토리 메서드
    public static UserInfoRes from(User user) {
        return UserInfoRes.builder()
                .username(user.getUsername())
                .profileImage(user.getProfileImage())
                .isLeader(false)
                .build();
    }
}
