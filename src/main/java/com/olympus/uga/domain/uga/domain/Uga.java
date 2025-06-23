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

    @Column(nullable = false)
    private Integer currentGrowthDays; // 현재 성장 일수

    @Column(nullable = false)
    private Integer totalGrowthDays; // 총 성장 일수 (자연 + 먹이)

    @Column
    private LocalDate completeGrowthDate; // 성장 완료한 날

    @Column(nullable = false)
    private String familyCode;

    // 성장도 업데이트 메서드
    public void updateGrowth(int additionalDays) {
        this.totalGrowthDays += additionalDays;
        this.currentGrowthDays += additionalDays;
        updateGrowthStage();
    }

    // 자연 성장 (하루 1일)
    public void naturalGrowth() {
        this.currentGrowthDays += 1;
        this.totalGrowthDays += 1;
        updateGrowthStage();
    }

    // 성장 단계 업데이트
    private void updateGrowthStage() {
        if (currentGrowthDays >= 365) {
            this.growth = UgaGrowth.INDEPENDENCE;
            this.completeGrowthDate = LocalDate.now();
        } else if (currentGrowthDays >= 274) {
            this.growth = UgaGrowth.ADULT;
        } else if (currentGrowthDays >= 183) {
            this.growth = UgaGrowth.TEENAGER;
        } else if (currentGrowthDays >= 92) {
            this.growth = UgaGrowth.CHILD;
        } else {
            this.growth = UgaGrowth.BABY;
        }
    }

    // 독립 처리
    public void makeIndependence() {
        this.growth = UgaGrowth.INDEPENDENCE;
        this.completeGrowthDate = LocalDate.now();
    }
}
