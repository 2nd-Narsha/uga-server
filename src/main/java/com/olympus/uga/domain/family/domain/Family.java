package com.olympus.uga.domain.family.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@SuperBuilder
@Entity
@Table(name = "tb_family")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Family {
    @Id @Column(nullable = false, unique = true)
    private String familyCode;

    @Column(nullable = false)
    private String familyName;

    @Column(nullable = false)
    private Long leaderId;

    @Column
    private String profileImage;

    @Column
    private Long presentUgaId; // 현재 우가

    @Column
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(name = "tb_family_member")
    private List<Long> memberList;

    public void updateLeader(Long id) {
        this.leaderId = id;
    }
}
