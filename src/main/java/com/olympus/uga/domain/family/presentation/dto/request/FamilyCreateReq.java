package com.olympus.uga.domain.family.presentation.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FamilyCreateReq {
    private String familyName;
    private String profileLink;
    private int presentUgaId;
}
