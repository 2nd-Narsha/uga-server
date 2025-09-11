package com.olympus.uga.domain.point.service;

import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.point.domain.enums.ActivityType;
import com.olympus.uga.domain.point.domain.enums.PointPackage;
import com.olympus.uga.domain.point.error.PaymentErrorCode;
import com.olympus.uga.domain.point.presentation.dto.request.PaymentReq;
import com.olympus.uga.domain.point.presentation.dto.response.PaymentRes;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static com.olympus.uga.domain.point.domain.enums.PaymentType.TOSS;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserJpaRepo userJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final FamilyJpaRepo familyJpaRepo;

    private static final Map<ActivityType, Integer> ACTIVITY_POINTS = Map.of(
            ActivityType.SIGN_UP, 50,
            ActivityType.LETTER, 2,
            ActivityType.ANSWER, 3,
            ActivityType.BIRTHDAY, 10
    );

    public int getPoint() {
        User user = userSessionHolder.getUser();

        return user.getPoint();
    }

    @Transactional
    public Response earnPoint(ActivityType activityType) {
        User user = userSessionHolder.getUser();

        int earnedPoints = ACTIVITY_POINTS.getOrDefault(activityType, 0);
        user.earnPoint(earnedPoints);

        userJpaRepo.save(user);

        return Response.ok("현재 포인트: " + user.getPoint());
    }

    // 포인트 구매 관련
    @Transactional
    public PaymentRes initiatePayment(PaymentReq request) {
        User user = userSessionHolder.getUser();

        // 결제 ID 생성 (UUID 등 사용)
        String paymentId = UUID.randomUUID().toString();

        // 선택한 PG사에 따른 결제 요청
        return switch (request.paymentType()) {
            case TOSS -> initiateTossPayment(paymentId, request.pointPackage(), user);
            case KAKAO -> initiateKakaoPayment(paymentId, request.pointPackage(), user);
        };
    }

    private PaymentRes initiateTossPayment(String paymentId, PointPackage pointPackage, User user) {
        // 토스페이먼츠 API 호출
        // 실제 구현시에는 토스페이먼츠 SDK 사용

        return new PaymentRes(
                paymentId,
                "https://api.tosspayments.com/v1/payments/" + paymentId,
                pointPackage.getPoints(),
                pointPackage.getPrice(),
                "PENDING"
        );
    }

    private PaymentRes initiateKakaoPayment(String paymentId, PointPackage pointPackage, User user) {
        // 카카오페이 API 호출
        // 실제 구현시에는 카카오페이 SDK 사용

        return new PaymentRes(
                paymentId,
                "https://kapi.kakao.com/v1/payment/ready",
                pointPackage.getPoints(),
                pointPackage.getPrice(),
                "PENDING"
        );
    }

    @Transactional
    public Response confirmPayment(String paymentId, String paymentKey) {
        User user = userSessionHolder.getUser();

        // 결제 검증 로직 (PG사 API를 통해 실제 결제 완료 확인)
        boolean isPaymentValid = validatePayment(paymentId, paymentKey);

        if (!isPaymentValid) {
            throw new CustomException(PaymentErrorCode.PAYMENT_VERIFICATION_FAILED);
        }

        // 결제가 완료된 패키지 정보 조회 (실제로는 DB에서 조회)
        PointPackage packageInfo = getPaymentPackage(paymentId);

        // 포인트 충전
        user.earnPoint(packageInfo.getPoints());
        userJpaRepo.save(user);

        return Response.ok("포인트 충전 완료! 현재 포인트: " + user.getPoint());
    }
}
