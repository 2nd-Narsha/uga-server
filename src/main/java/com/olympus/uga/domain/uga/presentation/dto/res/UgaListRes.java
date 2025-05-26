package com.olympus.uga.domain.uga.presentation.dto.res;

import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;

public class UgaListRes {
    private Long ugaId;
    private String name;
    private UgaGrowth growth;

    public UgaListRes(Uga uga) {
        this.ugaId = uga.getUgaId();
        this.name = uga.getName();
        this.growth = uga.getGrowth();
    }
}
