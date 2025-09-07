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

@RestController
@RequiredArgsConstructor
@RequestMapping("/family")
public class FamilyController {
    private final FamilyService familyService;

    @PostMapping(value = "/create")
    @Operation(summary = "가족 생성")
    public ResponseData<String> createFamily(@RequestBody FamilyCreateReq req) {
        return familyService.createFamily(req);
    }

    @PostMapping("/join")
    @Operation(summary = "가족 가입")
    public Response joinFamily(@RequestParam String familyCode) {
        return familyService.joinFamily(familyCode);
    }

    @GetMapping
    @Operation(summary = "가족 정보 불러오기")
    public ResponseData<FamilyInfoRes> getFamily() {
        return familyService.getFamily();
    }

    @PostMapping("/leave")
    @Operation(summary = "가족 떠나기")
    public Response leaveFamily(String familyCode) {
        return familyService.leaveFamily(familyCode);
    }

    @PostMapping("/change-leader")
    @Operation(summary = "가족 리더 넘기기", description = "id: 넘겨줄 구성원의 아이디")
    public Response changeLeader(@RequestBody LeaderChangeReq req) {
        return familyService.changeLeader(req);
    }

    @DeleteMapping("/remove")
    @Operation(summary = "가족 삭제")
    public Response removeFamily() {
        return familyService.deleteFamily();
    }
}
