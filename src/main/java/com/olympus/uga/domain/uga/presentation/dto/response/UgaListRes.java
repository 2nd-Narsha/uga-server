package com.olympus.uga.domain.uga.presentation.dto.response;

import java.time.LocalDate;

public record UgaListRes(Long ugaId, String ugaName, LocalDate completeGrowthTime) {
}
