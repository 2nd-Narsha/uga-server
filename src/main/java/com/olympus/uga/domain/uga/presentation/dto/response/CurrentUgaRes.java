package com.olympus.uga.domain.uga.presentation.dto.response;

import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;

public record CurrentUgaRes(String ugaName, UgaGrowth growth) {
}
