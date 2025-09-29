package com.olympus.uga.domain.mission.scheduler;

import com.olympus.uga.domain.mission.service.MissionAssignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MissionScheduler {
    private final MissionAssignService missionAssignService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void scheduleDailyMissions() {
        log.info("일일 미션 스케줄러 실행");
        try {
            missionAssignService.assignDailyMissions();
        } catch (Exception e) {
            log.error("일일 미션 갱신 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")
    public void scheduleWeeklyMissions() {
        log.info("주간 미션 스케줄러 실행");
        try {
            missionAssignService.assignWeeklyMissions();
        } catch (Exception e) {
            log.error("주간 미션 갱신 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
