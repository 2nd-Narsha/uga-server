package com.olympus.uga.domain.mission.presentation;

import com.olympus.uga.domain.mission.domain.enums.ActionType;
import com.olympus.uga.domain.mission.presentation.dto.response.MissionListRes;
import com.olympus.uga.domain.mission.service.MissionService;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mission")
public class MissionController {
    private final MissionService missionService;

    @GetMapping("/list")
    @Operation(summary = "미션 목록 조회")
    public ResponseData<MissionListRes> getMissionList() {
        return missionService.getMissionList();
    }

    @PostMapping("/claim-reward/{missionId}")
    @Operation(summary = "미션 보상 수령")
    public Response claimReward(@PathVariable Long missionId) {
        return missionService.claimReward(missionId);
    }

    @PostMapping("/claim-daily-bonus")
    @Operation(summary = "일일 미션 완주 보너스 수령")
    public Response claimDailyBonus() {
        return missionService.claimDailyBonus();
    }

    @PostMapping("/assign")
    @Operation(summary = "현재 사용자에게 미션 할당 (테스트용)")
    public Response assignMissionsToCurrentUser() {
        return missionService.assignMissionsToCurrentUser();
    }

    @PostMapping("/progress/{actionType}")
    @Operation(summary = "현재 사용자 미션 진행도 업데이트")
    public Response updateMissionProgress(@PathVariable ActionType actionType) {
        missionService.updateCurrentUserMissionProgress(actionType);
        return Response.ok("미션 진행도가 업데이트되었습니다.");
    }
}