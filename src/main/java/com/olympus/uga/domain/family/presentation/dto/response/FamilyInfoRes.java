package com.olympus.uga.domain.family.presentation.dto.response;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public class FamilyInfoRes {

    private String familyCode;
    private String familyName;
    private Long presentUgaId;
    private String profileLink;
    private Long representativeId;
    private List<User> memberList;
    private LocalDateTime createdAt;

    public FamilyInfoRes(Family family, List<User> memberList) {
        this.familyCode = family.getFamilyCode();
        this.familyName = family.getFamilyName();
        this.presentUgaId = family.getPresentUgaId();
        this.profileLink = family.getProfileLink();
        this.representativeId = family.getRepresentativeId();
        this.memberList = memberList;
        this.createdAt = family.getCreatedAt();

    }
}
