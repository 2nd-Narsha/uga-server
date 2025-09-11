package com.olympus.uga.domain.family.presentation.dto.response;

import com.olympus.uga.domain.user.domain.enums.UserCharacter;

import java.util.List;

public record FamilyInfoRes(
        String familyName,
        String familyCode,
        String profileImage,
        Long leaderId,
        List<FamilyMemberInfo> members
) {
    public record FamilyMemberInfo(
            Long id,
            String username,
            String profileImage,
            String imageUrl,
            String birth,
            List<String> interests,
            UserCharacter character,
            boolean isLeader
    ) {}
}