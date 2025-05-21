package com.olympus.uga.domain.family.presentation.dto.response;

import com.olympus.uga.domain.family.domain.Family;

import java.time.LocalDateTime;
import java.util.List;

public class FamilyInfoRes {

    private String familyCode;
    private String familyName;
    private int presentUgaId;
    private String profileLink;
    private String representativePhoneNum;
    private List<String> familyUgaList;
    private List<String> memberList;
    private LocalDateTime createdAt;

    public FamilyInfoRes(Family family) {
        this.familyCode = family.getFamilyCode();
        this.familyName = family.getFamilyName();
        this.presentUgaId = family.getPresentUgaId();
        this.profileLink = family.getProfileLink();
        this.representativePhoneNum = family.getRepresentativePhoneNum();
        this.familyUgaList = family.getFamilyUgaList();
        this.memberList = family.getMemberList();
        this.createdAt = family.getCreatedAt();

    }
}
