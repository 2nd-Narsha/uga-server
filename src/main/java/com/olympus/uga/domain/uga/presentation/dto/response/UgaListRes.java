package com.olympus.uga.domain.uga.presentation.dto.response;

import java.time.LocalDateTime;

public record UgaListRes(String ugaName, LocalDateTime completeGrowthTime) {
}
