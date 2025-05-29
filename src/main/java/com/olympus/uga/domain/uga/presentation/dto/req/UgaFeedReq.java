package com.olympus.uga.domain.uga.presentation.dto.req;

import com.olympus.uga.domain.uga.domain.enums.FoodType;
import lombok.Data;

@Data
public class UgaFeedReq {
    private Long ugaId;
    private FoodType foodType;
}
