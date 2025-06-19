package com.olympus.uga.domain.uga.domain;

import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_uga")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Uga {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ugaName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UgaGrowth growth; // 우가 성장도

    @Column(nullable = false)
    private LocalDate createdAt;

    @Column
    private LocalDate completeGrowthTime;

    @Column(nullable = false)
    private String familyCode;
}
