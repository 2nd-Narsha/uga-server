package com.olympus.uga.domain.uga.presentation.dto.request;

import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;

import java.time.LocalDateTime;

public record UgaCreateReq(String ugaName, String familyCode) {
    public static Uga fromUgaCreateReq(UgaCreateReq req) {
        return Uga.builder()
                .name(req.ugaName)
                .growth(UgaGrowth.BABY)
                .createdAt(LocalDateTime.now())
                .completeGrowthTime(LocalDateTime.now().plusDays(365))
                .build();
    }
}