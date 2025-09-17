package com.olympus.uga.domain.memo.presentation.dto.response;

import com.olympus.uga.domain.user.domain.User;
import lombok.Builder;

@Builder
public record MemoInfoRes(Long id, String profileImage, String imageUrl, String username, Double contribution, String memo, String location) {
    public static MemoInfoRes from(Long id, User user, Double contribution, String memo, String location) {
        return MemoInfoRes.builder()
                .id(id)
                .profileImage(user.getProfileImage())
                .imageUrl(user.getCharacter().getImageUrl())
                .username(user.getUsername())
                .contribution(contribution)
                .memo(memo)
                .location(location)
                .build();
    }
}