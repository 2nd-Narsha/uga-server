package com.olympus.uga.domain.family.presentation;

import com.olympus.uga.domain.family.presentation.dto.request.FamilyCreateReq;
import com.olympus.uga.domain.family.presentation.dto.response.FamilyInfoRes;
import com.olympus.uga.domain.family.service.FamilyService;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/family")
public class FamilyController {
    private final FamilyService familyService;

    @PostMapping("/create")
    public Response createFamily(@RequestBody FamilyCreateReq req) {
        return familyService.createFamily(req);
    }

    @PostMapping("/join")
    public Response joinFamily(@RequestBody String familyCode) {
        return familyService.joinFamily(familyCode);
    }

    @GetMapping("/members")
    public FamilyInfoRes getMembers(@RequestBody String familyCode) {
        return familyService.getFamily(familyCode);
    }

    @PostMapping("/leave")
    public Response leaveFamily(@RequestBody String familyCode) {
        return familyService.leaveFamily(familyCode);
    }
}
