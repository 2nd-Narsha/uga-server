package com.olympus.uga.domain.uga.presentation.dto.response;

import com.olympus.uga.domain.uga.domain.Uga;

import java.time.LocalDate;

public record UgaListRes(Long ugaId, String ugaName, LocalDate completeGrowthTime) {
    public static UgaListRes from(Uga uga) {
        return new UgaListRes(
                uga.getId(),
                uga.getUgaName(),
                uga.getCompleteGrowthTime()
        );
    }
}
