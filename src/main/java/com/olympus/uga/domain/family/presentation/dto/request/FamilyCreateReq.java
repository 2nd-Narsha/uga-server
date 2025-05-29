package com.olympus.uga.domain.family.presentation.dto.request;

import com.olympus.uga.domain.family.domain.Family;

public record FamilyCreateReq(String familyName) {
    public static Family fromFamilyCreateReq(String code, FamilyCreateReq req, Long id, String familyProfile){
        return Family.builder()
                .familyCode(code)
                .familyName(req.familyName)
                .leaderId(id)
                .profileImage(familyProfile)
                .build();
    }
}
