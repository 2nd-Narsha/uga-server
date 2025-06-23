package com.olympus.uga.domain.uga.service.helper;

import com.olympus.uga.domain.uga.domain.UgaContribution;
import com.olympus.uga.domain.uga.domain.repo.UgaContributionJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UgaContributionCalculator {
    private final UgaContributionJpaRepo ugaContributionJpaRepo;

    public Double calculateContributionRate(Long ugaId, Long userId) {
        UgaContribution userContribution = ugaContributionJpaRepo.findByUgaIdAndUserId(ugaId, userId)
                .orElse(UgaContribution.create(ugaId, userId));

        Integer totalContributedDays = ugaContributionJpaRepo.getTotalContributedDaysByUgaId(ugaId);

        // 아무도 먹이를 주지 않은 경우 (자연 성장만 있는 경우)
        if (totalContributedDays == null || totalContributedDays == 0) {
            return 0.0; // 먹이 기여가 0%
        }

        // 사용자의 기여도 계산 (퍼센트)
        return (double) userContribution.getContributedDays() / totalContributedDays * 100;
    }
}
