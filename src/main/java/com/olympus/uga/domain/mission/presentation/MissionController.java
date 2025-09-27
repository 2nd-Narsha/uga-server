package com.olympus.uga.domain.mission.presentation;

import com.olympus.uga.domain.mission.presentation.dto.response.MissionListRes;
import com.olympus.uga.domain.mission.service.MissionService;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
