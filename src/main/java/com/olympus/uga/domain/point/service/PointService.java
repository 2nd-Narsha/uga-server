package com.olympus.uga.domain.point.service;

import com.olympus.uga.domain.point.domain.enums.ActivityType;
import com.olympus.uga.domain.point.domain.repo.PurchaseJpaRepo;
import com.olympus.uga.domain.point.error.PointErrorCode;
import com.olympus.uga.domain.point.presentation.dto.request.PurchaseReq;
import com.olympus.uga.domain.point.presentation.dto.response.PointRewardRes;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import com.olympus.uga.global.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserJpaRepo userJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final PurchaseJpaRepo purchaseJpaRepo;
    private final WebSocketService webSocketService;

    private static final Map<ActivityType, Integer> ACTIVITY_POINTS = Map.of(
            ActivityType.LETTER, 2,
            ActivityType.ANSWER, 3,
            ActivityType.BIRTHDAY, 10
    );

    @Transactional(readOnly = true)
    public int getPoint() {
        User user = userSessionHolder.getUser();

        return user.getPoint();
    }

    @Transactional
    public Response earnPoint(ActivityType activityType) {
        User user = userSessionHolder.getUser();

        int earnedPoints = ACTIVITY_POINTS.getOrDefault(activityType, 0);
        user.earnPoint(earnedPoints);
        user.updateLastActivityAt(); // 활동 시간 업데이트

        userJpaRepo.save(user);

        // 웹소켓으로 실시간 포인트 변경 알림
        if (user.getFamilyCode() != null) {
            webSocketService.notifyPointUpdate(
                user.getFamilyCode(),
                user.getId(),
                user.getPoint(),
                activityType.name()
            );
        }

        return Response.ok("현재 포인트: " + user.getPoint());
    }

    @Transactional
    public ResponseData<PointRewardRes> rewardPointsFromPurchase(PurchaseReq req) {
        User user = userSessionHolder.getUser();

        // 중복 구매 체크
        if (purchaseJpaRepo.existsByPurchaseTokenHash(req.getHashedToken())) {
            throw new CustomException(PointErrorCode.DUPLICATE_PURCHASE);
        }

        // 포인트 지급
        int earnedPoints = req.pointPackage().getPoints();
        user.earnPoint(earnedPoints);
        userJpaRepo.save(user);

        purchaseJpaRepo.save(PurchaseReq.toPurchaseRecord(user, req));

        PointRewardRes successDto = new PointRewardRes(earnedPoints, user.getPoint());
        return ResponseData.ok("포인트 충전에 성공하였습니다.", successDto);
    }
}
