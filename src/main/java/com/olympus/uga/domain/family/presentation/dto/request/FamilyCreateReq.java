package com.olympus.uga.domain.family.presentation.dto.request;

import lombok.Data;

@Data
public class FamilyCreateReq {
    private String familyName;
    private int presentUgaId;
}
