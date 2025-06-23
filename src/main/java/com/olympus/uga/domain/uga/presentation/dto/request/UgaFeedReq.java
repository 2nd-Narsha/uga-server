package com.olympus.uga.domain.uga.presentation.dto.request;

import com.olympus.uga.domain.uga.domain.enums.FoodType;

public record UgaFeedReq(Long ugaId, FoodType foodType) {
}
