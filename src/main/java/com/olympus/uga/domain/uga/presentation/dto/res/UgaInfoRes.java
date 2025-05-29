package com.olympus.uga.domain.uga.presentation.dto.res;

import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;

import java.time.LocalDateTime;

public class UgaInfoRes {

    private Long ugaId;
    private String name;
    private UgaGrowth growth;
    private LocalDateTime createdAt;
    private LocalDateTime completeGrowthTime;

    public UgaInfoRes(Uga uga) {
        this.ugaId = uga.getUgaId();
        this.name = uga.getName();
        this.growth = uga.getGrowth();
        this.createdAt = uga.getCreatedAt();
        this.completeGrowthTime = uga.getCompleteGrowthTime();
    }
}
