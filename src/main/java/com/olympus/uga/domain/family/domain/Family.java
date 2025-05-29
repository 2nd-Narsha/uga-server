package com.olympus.uga.domain.family.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
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
    private String profileLink;

    @Column
    private Long presentUgaId; // 현재 우가

    @Column
    private LocalDateTime createdAt;
}
