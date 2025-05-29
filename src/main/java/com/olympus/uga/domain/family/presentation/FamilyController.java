package com.olympus.uga.domain.family.presentation;

import com.olympus.uga.domain.family.presentation.dto.request.FamilyCreateReq;
import com.olympus.uga.domain.family.presentation.dto.request.LeaderChangeReq;
import com.olympus.uga.domain.family.presentation.dto.response.FamilyInfoRes;
import com.olympus.uga.domain.family.service.FamilyService;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/family")
public class FamilyController {
    private final FamilyService familyService;

    @PostMapping("/create")
    public ResponseData<String> createFamily(@RequestPart MultipartFile familyProfile, @RequestBody FamilyCreateReq req) {
        return familyService.createFamily(familyProfile, req);
    }

    @PostMapping("/join")
    public Response joinFamily(@RequestParam String familyCode) {
        return familyService.joinFamily(familyCode);
    }

    @GetMapping("/members")
    public FamilyInfoRes getMembers() {
        return familyService.getFamily();
    }

    @PostMapping("/leave")
    public Response leaveFamily() {
        return familyService.leaveFamily();
    }

    @PostMapping("/change-leader")
    public Response changeLeader(@RequestBody LeaderChangeReq req) {
        return familyService.changeLeader(req);
    }
}
