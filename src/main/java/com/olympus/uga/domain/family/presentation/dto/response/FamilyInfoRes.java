package com.olympus.uga.domain.family.presentation.dto.response;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;

import java.time.LocalDateTime;
import java.util.List;

public class FamilyInfoRes {

    private String familyCode;
    private String familyName;
    private Long presentUgaId;
    private int presentUgaId;
    private int point;
    private String profileLink;
    private String representativePhoneNum;
    private List<User> memberList;
    private LocalDateTime createdAt;

    public FamilyInfoRes(Family family, List<User> memberList) {
        this.familyCode = family.getFamilyCode();
        this.familyName = family.getFamilyName();
        this.presentUgaId = family.getPresentUgaId();
        this.point = family.getPoint();
        this.profileLink = family.getProfileLink();
        this.representativePhoneNum = family.getRepresentativePhoneNum();
        this.memberList = memberList;
        this.createdAt = family.getCreatedAt();

    }
}
