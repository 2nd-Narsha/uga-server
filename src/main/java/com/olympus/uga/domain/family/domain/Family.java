package com.olympus.uga.domain.family.domain;

import com.olympus.uga.domain.family.presentation.dto.request.FamilyCreateReq;
import com.olympus.uga.domain.family.util.CodeGenerator;
import com.olympus.uga.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tb_family")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Family {

    @Id
    @Column
    private String familyCode;

    @Column
    private String familyName;

    @Column
    private Long presentUgaId;

    @Column
    private String profileLink;

    @Column
    private String representativePhoneNum;

    @ElementCollection
    @CollectionTable(name = "tb_family_member")
    private List<String> memberList;

    @Column
    private LocalDateTime createdAt;

    public Family(FamilyCreateReq req, String ProfileLink, String familyCode) {
        this.familyCode = familyCode;
        this.familyName = req.getFamilyName();
        this.profileLink = profileLink;
        this.representativePhoneNum = SecurityContextHolder.getContext().getAuthentication().getName();
        this.memberList = new ArrayList<>();
        this.memberList.add(this.representativePhoneNum);
        this.createdAt = LocalDateTime.now();
    }
}
