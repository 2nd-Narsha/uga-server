package com.olympus.uga.domain.uga.presentation.dto.response;

import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;

import java.time.LocalDateTime;

public record UgaInfoRes(String UgaName, UgaGrowth growth, LocalDateTime createdAt, LocalDateTime completeGrowthTime) {
}
//    private Long ugaId;
//    private String name;
//    private UgaGrowth growth;
//    private LocalDateTime createdAt;
//    private LocalDateTime completeGrowthTime;