package com.olympus.uga.domain.uga.presentation.dto.response;

import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;

import java.time.LocalDate;

public record CurrentUgaRes(
        Long ugaId,
        String ugaName,
        UgaGrowth growth,
        Double growthRate, // 성장도
        LocalDate createdAt,
        Double myContributionRate
) {
    public static CurrentUgaRes from(Uga uga, Double contributionRate) {
        double growthRate = Math.min((uga.getCurrentGrowthDays() / 365.0) * 100, 100.0);
        return new CurrentUgaRes(
                uga.getId(),
                uga.getUgaName(),
                uga.getGrowth(),
                growthRate,
                uga.getCreatedAt(),
                contributionRate
        );
    }
}
