package com.olympus.uga.domain.point.presentation;

import com.olympus.uga.domain.point.domain.enums.ActivityType;
import com.olympus.uga.domain.point.presentation.dto.request.PaymentReq;
import com.olympus.uga.domain.point.presentation.dto.response.PaymentRes;
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
    @Operation(summary = "포인트 얻기", description = "SIGN_UP(회원가입), LETTER(편지작성), ATTENDANCE(하루출석), SEVENTH_ATTENDANCE(7일출석), ANSWER(답변), BIRTHDAY(생일)")
    public Response earnPoint(@RequestParam ActivityType activityType) {
        return pointService.earnPoint(activityType);
    }

    @PostMapping("/purchase")
    @Operation(summary = "포인트 구매")
    public ResponseData<PaymentRes> purchasePoint(@RequestBody PaymentReq request) {
        return Response.ok(pointService.initiatePayment(request));
    }

    @PostMapping("/purchase/confirm")
    @Operation(summary = "결제 확인", description = "결제 완료 후 포인트 충전")
    public Response confirmPayment(@RequestParam String paymentId,
                                   @RequestParam String paymentKey) {
        return pointService.confirmPayment(paymentId, paymentKey);
    }
}
