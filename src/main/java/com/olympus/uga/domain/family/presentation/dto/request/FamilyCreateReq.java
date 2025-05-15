package com.olympus.uga.domain.family.presentation.dto.request;

import lombok.Data;

@Data
public class FamilyCreateReq {
    private String familyName;
    private String profileLink;
    private int presentUgaId;
}
