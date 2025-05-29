package com.olympus.uga.domain.family.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

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

    public void updateLeader(Long id) {
        this.leaderId = id;
    }
}
