package com.olympus.uga.domain.uga.presentation.dto.response;

import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;

import java.time.LocalDate;

public record CurrentUgaRes(
        Long ugaId,
        String ugaName,
        UgaGrowth growth,
        Integer currentGrowthDays,
        Integer totalGrowthDays,
        LocalDate createdAt,
        Double myContributionRate
) {
    public static CurrentUgaRes from(Uga uga, Double contributionRate) {
        return new CurrentUgaRes(
                uga.getId(),
                uga.getUgaName(),
                uga.getGrowth(),
                uga.getCurrentGrowthDays(),
                uga.getTotalGrowthDays(),
                uga.getCreatedAt(),
                contributionRate
        );
    }
}
