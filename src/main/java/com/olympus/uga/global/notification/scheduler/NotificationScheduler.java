package com.olympus.uga.global.notification.scheduler;

import com.olympus.uga.domain.calendar.domain.DDay;
import com.olympus.uga.domain.calendar.domain.repo.DDayJpaRepo;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.notification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private final DDayJpaRepo dDayJpaRepo;
    private final UserJpaRepo userJpaRepo;
    private final PushNotificationService pushNotificationService;

    /**
     * 디데이 알림 스케줄러 (매 10분마다 실행)
     * startTime이 설정된 디데이의 시작시간 30분 전에 자동으로 알림 발송
     */
    @Scheduled(fixedRate = 600000, zone = "Asia/Seoul")
    public void sendDDayNotifications() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        List<DDay> dDaysToNotify = dDayJpaRepo.findByDateAndStartTimeNotNullAndIsNotificationSentFalse(today);

        for (DDay dDay : dDaysToNotify) {
            if (shouldSendNotification(dDay.getStartTime(), now)) {
                List<User> familyMembers = userJpaRepo.findByFamilyCodeWithFcmToken(dDay.getFamilyCode());

                for (User user : familyMembers) {
                    pushNotificationService.sendDdayReminderNotification(
                            user.getFcmToken(),
                            dDay.getTitle()
                    );
                }

                dDay.markNotificationSent();
                dDayJpaRepo.save(dDay);
                log.info("디데이 알림 전송 - 이벤트: {}, 시작시간: {}", dDay.getTitle(), dDay.getStartTime());
            }
        }
    }

    // 비활성 사용자 알림 스케줄러 (매일 오전 9시에 실행)
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void sendInactivityNotifications() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<User> inactiveUsers = userJpaRepo.findUsersInactiveForDays(sevenDaysAgo);

        for (User user : inactiveUsers) {
            pushNotificationService.sendInactivityNotification(
                    user.getFcmToken(),
                    user.getUsername()
            );
            log.info("비활성 사용자 알림 전송 - 사용자: {}", user.getId());
        }
    }

    private boolean shouldSendNotification(String startTime, LocalDateTime now) {
        if (startTime == null) return false;

        try {
            String[] timeParts = startTime.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            LocalDateTime startDateTime = now.toLocalDate().atTime(hour, minute);
            LocalDateTime thirtyMinutesBefore = startDateTime.minusMinutes(30);

            // 현재 시간이 30분 전 시간과 일치하는지 확인 (±5분 오차 허용)
            return Math.abs(java.time.Duration.between(now, thirtyMinutesBefore).toMinutes()) <= 5;
        } catch (Exception e) {
            log.error("시작시간 파싱 오류: {}", startTime, e);
            return false;
        }
    }
}