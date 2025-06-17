package com.olympus.uga.domain.family.presentation.dto.response;

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
            String birth,
            String interests,
            boolean isLeader
    ) {}
}