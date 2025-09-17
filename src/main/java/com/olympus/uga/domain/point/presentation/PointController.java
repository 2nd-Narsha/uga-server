package com.olympus.uga.domain.point.presentation;

import com.olympus.uga.domain.point.domain.enums.ActivityType;
import com.olympus.uga.domain.point.presentation.dto.request.PurchaseReq;
import com.olympus.uga.domain.point.presentation.dto.response.PointRewardRes;
import com.olympus.uga.domain.point.service.PointService;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {
    private final PointService pointService;

    @GetMapping
    @Operation(summary = "포인트 조회")
    public int getPoint() {
        return pointService.getPoint();
    }

    @PostMapping("/earn")
    @Operation(summary = "포인트 얻기", description = "LETTER(편지작성), ANSWER(답변), BIRTHDAY(생일)")
    public Response earnPoint(@RequestParam("activityType") ActivityType activityType) {
        return pointService.earnPoint(activityType);
    }

    @PostMapping("/reward")
    @Operation(summary = "인앱결제 포인트 지급")
    public ResponseData<PointRewardRes> rewardPointsFromPurchase(@RequestBody PurchaseReq req) {
        return pointService.rewardPointsFromPurchase(req);
    }
}
