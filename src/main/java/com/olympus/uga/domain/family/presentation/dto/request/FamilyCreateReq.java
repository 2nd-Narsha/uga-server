package com.olympus.uga.domain.family.presentation.dto.request;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.global.image.service.ImageService;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class FamilyCreateReq {

    private String familyName;
    private MultipartFile familyProfile;

    public static Family fromFamilyCreateReq(String code, FamilyCreateReq req, Long id, String familyProfile) {
        return Family.builder()
                .familyCode(code)
                .familyName(req.getFamilyName())
                .leaderId(id)
                .profileImage(familyProfile)
                .memberList(new ArrayList<>(List.of(id)))
                .build();
    }
}