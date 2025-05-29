package com.olympus.uga.domain.family.presentation;

import com.olympus.uga.domain.family.presentation.dto.request.FamilyCreateReq;
import com.olympus.uga.domain.family.presentation.dto.request.LeaderChangeReq;
import com.olympus.uga.domain.family.presentation.dto.response.FamilyInfoRes;
import com.olympus.uga.domain.family.service.FamilyService;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
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

    @GetMapping
    @Operation(summary = "가족 정보 불러오기")
    public ResponseData<FamilyInfoRes> getFamily() {
        return familyService.getFamily();
    }

    @PostMapping("/leave")
    public Response leaveFamily(String familyCode) {
        return familyService.leaveFamily(familyCode);
    }

    @PostMapping("/change-leader")
    @Operation(summary = "가족 리더 넘기기")
    public Response changeLeader(@RequestBody LeaderChangeReq req) {
        return familyService.changeLeader(req);
    }
}
