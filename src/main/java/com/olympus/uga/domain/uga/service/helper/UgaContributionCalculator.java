package com.olympus.uga.domain.uga.service.helper;

import com.olympus.uga.domain.uga.domain.UgaContribution;
import com.olympus.uga.domain.uga.domain.repo.UgaContributionJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UgaContributionCalculator {
    private final UgaContributionJpaRepo ugaContributionJpaRepo;

    /**
     * 사용자의 기여도를 백분율로 계산
     * 전체 성장 일수(자연 성장 + 먹이 성장) 대비 사용자의 먹이 기여도
     */
    public Double calculateContributionRate(Long ugaId, Long userId, int totalGrowthDays) {
        UgaContribution userContribution = ugaContributionJpaRepo.findByUgaIdAndUserId(ugaId, userId)
                .orElse(UgaContribution.create(ugaId, userId));

        // 전체 성장 일수가 0이면 기여도도 0%
        if (totalGrowthDays == 0) {
            return 0.0;
        }

        // 사용자가 먹이로 기여한 일수 / 전체 성장 일수 * 100
        return Math.round((double) userContribution.getContributedDays() / totalGrowthDays * 100 * 100.0) / 100.0;
    }
}
