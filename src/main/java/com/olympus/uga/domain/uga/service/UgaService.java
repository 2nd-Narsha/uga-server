package com.olympus.uga.domain.uga.service;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.UgaContribution;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;
import com.olympus.uga.domain.uga.domain.repo.UgaContributionJpaRepo;
import com.olympus.uga.domain.uga.domain.repo.UgaJpaRepo;
import com.olympus.uga.domain.uga.error.UgaErrorCode;
import com.olympus.uga.domain.uga.presentation.dto.request.UgaCreateReq;
import com.olympus.uga.domain.uga.presentation.dto.request.UgaFeedReq;
import com.olympus.uga.domain.uga.presentation.dto.request.UgaIndependenceReq;
import com.olympus.uga.domain.uga.presentation.dto.response.CurrentUgaRes;
import com.olympus.uga.domain.uga.presentation.dto.response.UgaListRes;
import com.olympus.uga.domain.uga.service.helper.UgaContributionCalculator;
import com.olympus.uga.domain.uga.usecase.UgaUseCase;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.notification.service.PushNotificationService;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import com.olympus.uga.global.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UgaService {
    private final UgaJpaRepo ugaJpaRepo;
    private final FamilyJpaRepo familyJpaRepo;
    private final UgaContributionJpaRepo ugaContributionJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final UgaContributionCalculator contributionCalculator;
    private final UgaUseCase ugaUseCase;
    private final UserJpaRepo userJpaRepo;
    private final PushNotificationService pushNotificationService;

    @Transactional
    public Response createUga(UgaCreateReq req) {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        Uga uga = ugaJpaRepo.save(UgaCreateReq.fromUgaCreateReq(req, userFamilyCode));

        Family family = familyJpaRepo.findById(userFamilyCode)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));
        family.updatePresentUgaId(uga.getId());
        familyJpaRepo.save(family);

        // 가족 구성원들의 기여도 초기화
        initializeContributions(uga.getId(), family.getMemberList());

        return Response.created("우가가 성공적으로 생성되었습니다.");
    }

    public CurrentUgaRes getCurrentUga() {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        Family family = familyJpaRepo.findById(userFamilyCode)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        if (family.getPresentUgaId() == null) {
            throw new CustomException(UgaErrorCode.UGA_NOT_FOUND);
        }

        Uga currentUga = ugaJpaRepo.findById(family.getPresentUgaId())
                .orElseThrow(() -> new CustomException(UgaErrorCode.UGA_NOT_FOUND));

        // 기여도 계산 - 전체 성장 일수 대비 사용자의 먹이 기여도
        Double contributionRate = contributionCalculator.calculateContributionRate(
                currentUga.getId(), user.getId(), currentUga.getTotalGrowthDays());

        return CurrentUgaRes.from(currentUga, contributionRate);
    }

    public Response feedUga(UgaFeedReq req) {
        User user = userSessionHolder.getUser();
        return ugaUseCase.feedUga(req, user);
    }

    public Response setIndependence(UgaIndependenceReq req) {
        User user = userSessionHolder.getUser();
        return ugaUseCase.setIndependence(req, user);
    }

    public List<UgaListRes> getDictionary() {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        List<Uga> independenceUgas = ugaJpaRepo.findByFamilyCodeAndGrowth(userFamilyCode, UgaGrowth.INDEPENDENCE);

        return independenceUgas.stream()
                .map(UgaListRes::from)
                .toList();
    }

    private String getUserFamilyCode(Long userId) {
        Family family = familyJpaRepo.findByMemberListContaining(userId)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        return family.getFamilyCode();
    }

    private void initializeContributions(Long ugaId, List<Long> memberList) {
        for (Long memberId : memberList) {
            UgaContribution contribution = UgaContribution.create(ugaId, memberId);
            ugaContributionJpaRepo.save(contribution);
        }
    }
}
