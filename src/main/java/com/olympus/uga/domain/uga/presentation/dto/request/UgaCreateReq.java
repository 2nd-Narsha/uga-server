package com.olympus.uga.domain.uga.presentation.dto.request;

import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;

import java.time.LocalDate;

public record UgaCreateReq(String ugaName) {
    public static Uga fromUgaCreateReq(UgaCreateReq req, String familyCode) {
        return Uga.builder()
                .ugaName(req.ugaName)
                .growth(UgaGrowth.BABY)
                .createdAt(LocalDate.now())
                .familyCode(familyCode)
                .build();
    }
}
