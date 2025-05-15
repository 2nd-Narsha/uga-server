package com.olympus.uga.domain.family.domain;

import com.olympus.uga.domain.family.presentation.dto.request.FamilyCreateReq;
import com.olympus.uga.domain.family.util.CodeGenerator;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@SuperBuilder
@Table(name = "tb_family")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Family {

    @Id
    @Column
    private String familyCode;

    @Column
    private String familyName;

    @Column
    private int presentUgaId;

    @Column
    private String profileLink;

    @Column
    private String representativePhoneNum;

    @ElementCollection
    @CollectionTable(name="tb_family_uga")
    private List<String> familyUgaList;

    @ElementCollection
    @CollectionTable(name="tb_family_member")
    private List<String> memberList;

    @Column
    private LocalDateTime createdAt;

    public Family(FamilyCreateReq req, String familyCode) {
        this.familyCode = familyCode;
        this.familyName = req.getFamilyName();
        this.profileLink = req.getProfileLink();
        this.presentUgaId = req.getPresentUgaId();
        this.representativePhoneNum = SecurityContextHolder.getContext().getAuthentication().getName();
        this.memberList = new ArrayList<>();
        this.familyUgaList = new ArrayList<>();
        this.memberList.add(this.representativePhoneNum);
        this.createdAt = LocalDateTime.now();
    }
}
