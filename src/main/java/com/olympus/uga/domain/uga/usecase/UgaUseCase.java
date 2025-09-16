package com.olympus.uga.domain.uga.usecase;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.UgaContribution;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;
import com.olympus.uga.domain.uga.domain.repo.UgaContributionJpaRepo;
import com.olympus.uga.domain.uga.domain.repo.UgaJpaRepo;
import com.olympus.uga.domain.uga.error.UgaErrorCode;
import com.olympus.uga.domain.uga.presentation.dto.request.UgaFeedReq;
import com.olympus.uga.domain.uga.presentation.dto.request.UgaIndependenceReq;
import com.olympus.uga.domain.uga.util.UgaFeedUtil;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.notification.service.PushNotificationService;
import com.olympus.uga.global.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UgaUseCase {
    private final UgaJpaRepo ugaJpaRepo;
    private final UgaContributionJpaRepo ugaContributionJpaRepo;
    private final FamilyJpaRepo familyJpaRepo;
    private final UserJpaRepo userJpaRepo;
    private final WebSocketService webSocketService;
    private final PushNotificationService pushNotificationService;

    @Transactional
    public Response feedUga(UgaFeedReq req, User user) {
        Uga uga = ugaJpaRepo.findById(req.ugaId())
                .orElseThrow(() -> new CustomException(UgaErrorCode.UGA_NOT_FOUND));

        if (!uga.getFamilyCode().equals(uga.getFamilyCode())) {
            throw new CustomException(UgaErrorCode.NOT_FAMILY_UGA);
        }

        // 먹이를 줄 수 있는 상태인지 확인
        if (!UgaFeedUtil.canFeed(uga.getGrowth())) {
            if (uga.getGrowth() == UgaGrowth.ALL_GROWTH) {
                throw new CustomException(UgaErrorCode.UGA_FULLY_GROWN);
            } else if (uga.getGrowth() == UgaGrowth.INDEPENDENCE) {
                throw new CustomException(UgaErrorCode.UGA_ALREADY_INDEPENDENCE);
            }
        }

        // 유효한 먹이 타입인지 확인
        if (!UgaFeedUtil.isValidFoodType(req.foodType())) {
            throw new CustomException(UgaErrorCode.INVALID_FOOD_TYPE);
        }

        Family family = familyJpaRepo.findById(uga.getFamilyCode())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        // 포인트 차감
        int totalCost = UgaFeedUtil.calculateTotalCost(req.foodType(), family.getMemberList().size());
        user.usePoint(totalCost);
        user.updateLastActivityAt(); // 활동 시간 업데이트
        userJpaRepo.save(user);

        // 우가 성장 처리
        int growthDays = UgaFeedUtil.getGrowthDays(req.foodType());
        UgaGrowth previousGrowth = uga.getGrowth();
        uga.updateGrowth(growthDays);
        ugaJpaRepo.save(uga);

        // 기여도 업데이트
        updateContribution(uga.getId(), user.getId(), growthDays);

        // 웹소켓으로 실시간 업데이트 알림 (이미 선언된 userFamilyCode 변수 사용)

        // 우가 성장도 변경 알림
        if (!previousGrowth.equals(uga.getGrowth())) {
            webSocketService.notifyUgaGrowthUpdate(user.getFamilyCode(), uga);

            // 가족들에게 우가 성장 푸시 알림 전송
            sendUgaGrowthNotification(family, uga);
        }
        
        // 포인트 변경 알림
        webSocketService.notifyPointUpdate(uga.getFamilyCode(), user.getId(), user.getPoint(), "UGA_FEED");
        
        // 기여도 변경 알림
        UgaContribution contribution = ugaContributionJpaRepo.findByUgaIdAndUserId(uga.getId(), user.getId())
            .orElse(null);
        if (contribution != null) {
            webSocketService.notifyContributionUpdate(user.getFamilyCode(), user.getId(), contribution);
        }

        return Response.ok("먹이를 성공적으로 주었습니다. 현재 포인트: " + user.getPoint());
    }

    @Transactional
    public Response setIndependence(UgaIndependenceReq req, User user) {
        Uga uga = ugaJpaRepo.findById(req.ugaId())
                .orElseThrow(() -> new CustomException(UgaErrorCode.UGA_NOT_FOUND));

        if (!uga.getFamilyCode().equals(user.getFamilyCode())) {
            throw new CustomException(UgaErrorCode.NOT_FAMILY_UGA);
        }

        // 다 자란 상태인지 확인
        if (uga.getGrowth() != UgaGrowth.ALL_GROWTH) {
            throw new CustomException(UgaErrorCode.UGA_NOT_FULLY_GROWN);
        }

        if (req.independence()) {
            uga.makeIndependence();
            ugaJpaRepo.save(uga);

            // 가족의 현재 우가를 null로 설정
            Family family = familyJpaRepo.findById(user.getFamilyCode())
                    .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));
            family.updatePresentUgaId(null);
            familyJpaRepo.save(family);

            return Response.ok("우가가 성공적으로 독립하였습니다.");
        } else {
            return Response.ok("우가와 계속 함께 하기로 했습니다.");
        }
    }

    private void updateContribution(Long ugaId, Long userId, int contributedDays) {
        UgaContribution contribution = ugaContributionJpaRepo.findByUgaIdAndUserId(ugaId, userId)
                .orElse(UgaContribution.create(ugaId, userId));

        contribution.addContribution(contributedDays);
        ugaContributionJpaRepo.save(contribution);
    }


    // 우가 성장 시 가족들에게 푸시 알림 전송
    private void sendUgaGrowthNotification(Family family, Uga uga) {
        try {
            // 가족 구성원들의 FCM 토큰 가져오기
            List<User> familyMembers = userJpaRepo.findAllById(family.getMemberList());

            // 우가 성장 단계를 숫자로 변환
            int growthLevel = getGrowthLevel(uga.getGrowth());

            for (User member : familyMembers) {
                if (member.getFcmToken() != null) {
                    pushNotificationService.sendUgaGrowthNotification(
                        member.getFcmToken(),
                        growthLevel,
                        uga.getUgaName()
                    );
                }
            }
        } catch (Exception e) {
            // 푸시 알림 실패해도 우가 성장은 성공으로 처리
            log.warn("우가 성장 푸시 알림 전송 실패: {}", e.getMessage());
        }
    }

    private int getGrowthLevel(UgaGrowth growth) {
        return switch (growth) {
            case BABY -> 1;
            case CHILD -> 2;
            case TEENAGER -> 3;
            case ADULT -> 4;
            case ALL_GROWTH -> 5;
            case INDEPENDENCE -> 6;
            default -> 1;
        };
    }
}
