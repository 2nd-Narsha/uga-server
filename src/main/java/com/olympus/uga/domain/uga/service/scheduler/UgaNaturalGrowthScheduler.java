package com.olympus.uga.domain.uga.service.scheduler;

import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;
import com.olympus.uga.domain.uga.domain.repo.UgaJpaRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UgaNaturalGrowthScheduler {
    private final UgaJpaRepo ugaJpaRepo;

    /**
     * 매일 자정에 실행되는 자연 성장 스케줄러
     * 독립하지 않은 모든 우가들의 자연 성장을 처리
     */
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    @Transactional
    public void processNaturalGrowth() {
        log.info("우가 자연 성장 스케줄러 시작");

        try {
            // 독립하지 않은 모든 우가 조회 (INDEPENDENCE 상태가 아닌 우가들)
            List<Uga> activeUgas = ugaJpaRepo.findByGrowthNot(UgaGrowth.INDEPENDENCE);

            for (Uga uga : activeUgas) {
                // 이미 완전히 자란 우가는 자연 성장하지 않음
                if (uga.getGrowth() != UgaGrowth.ALL_GROWTH) {
                    uga.naturalGrowth();
                    log.debug("우가 자연 성장 처리: ID={}, 현재 성장일수={}, 성장단계={}",
                            uga.getId(), uga.getCurrentGrowthDays(), uga.getGrowth());
                }
            }

            // 일괄 저장
            ugaJpaRepo.saveAll(activeUgas);

            log.info("우가 자연 성장 스케줄러 완료: {} 마리 처리", activeUgas.size());

        } catch (Exception e) {
            log.error("우가 자연 성장 스케줄러 실행 중 오류 발생", e);
        }
    }
}