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
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UgaUseCase {
    private final UgaJpaRepo ugaJpaRepo;
    private final UgaContributionJpaRepo ugaContributionJpaRepo;
    private final FamilyJpaRepo familyJpaRepo;

    @Transactional
    public Response feedUga(UgaFeedReq req, User user) {
        Uga uga = ugaJpaRepo.findById(req.ugaId())
                .orElseThrow(() -> new CustomException(UgaErrorCode.UGA_NOT_FOUND));

        // 가족 권한 확인
        String userFamilyCode = getUserFamilyCode(user.getId());
        if (!uga.getFamilyCode().equals(userFamilyCode)) {
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

        Family family = familyJpaRepo.findById(userFamilyCode)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        // 비용 계산 및 포인트 차감
        int totalCost = UgaFeedUtil.calculateTotalCost(req.foodType(), family.getMemberList().size());
        user.usePoint(totalCost);

        // 우가 성장 처리
        int growthDays = UgaFeedUtil.getGrowthDays(req.foodType());
        uga.updateGrowth(growthDays);
        ugaJpaRepo.save(uga);

        // 기여도 업데이트
        updateContribution(uga.getId(), user.getId(), growthDays);

        return Response.ok("먹이를 성공적으로 주었습니다. 현재 포인트: " + user.getPoint());
    }

    @Transactional
    public Response setIndependence(UgaIndependenceReq req, User user) {
        Uga uga = ugaJpaRepo.findById(req.ugaId())
                .orElseThrow(() -> new CustomException(UgaErrorCode.UGA_NOT_FOUND));

        String userFamilyCode = getUserFamilyCode(user.getId());
        if (!uga.getFamilyCode().equals(userFamilyCode)) {
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
            Family family = familyJpaRepo.findById(userFamilyCode)
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

    private String getUserFamilyCode(Long userId) {
        Family family = familyJpaRepo.findByMemberListContaining(userId)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        return family.getFamilyCode();
    }
}
