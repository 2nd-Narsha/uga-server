package com.olympus.uga.domain.uga.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_uga_contribution")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UgaContribution { // 우가 키우기 기여도
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ugaId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer contributedDays = 0; // 기여한 일수

    public void addContribution(int days) {
        this.contributedDays += days;
    }

    public static UgaContribution create(Long ugaId, Long userId) {
        return UgaContribution.builder()
                .ugaId(ugaId)
                .userId(userId)
                .contributedDays(0)
                .build();
    }
}
