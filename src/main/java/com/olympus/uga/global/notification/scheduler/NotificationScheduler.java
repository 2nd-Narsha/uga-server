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
     * 디데이 알림 스케줄러 (매 5분마다 실행)
     * startTime이 설정된 디데이의 시작시간 30분 전에 자동으로 알림 발송
     */
    @Scheduled(cron = "0 */5 * * * *", zone = "Asia/Seoul")
    public void sendDDayNotifications() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        log.info("디데이 알림 스케줄러 실행 - 현재시간: {}", now);

        List<DDay> dDaysToNotify = dDayJpaRepo.findByDateAndStartTimeNotNullAndIsNotificationSentFalse(today);

        if (dDaysToNotify.isEmpty()) {
            log.info("오늘 알림을 보낼 디데이가 없습니다.");
            return;
        }

        log.info("오늘 알림 대상 디데이 개수: {}", dDaysToNotify.size());

        for (DDay dDay : dDaysToNotify) {
            log.info("디데이 처리 중 - 제목: {}, 시작시간: {}, 가족코드: {}",
                    dDay.getTitle(), dDay.getStartTime(), dDay.getFamilyCode());

            boolean shouldSend = shouldSendNotification(dDay.getStartTime(), now);
            log.info("알림 전송 조건 확인 - 전송여부: {}, 이벤트: {}", shouldSend, dDay.getTitle());

            if (shouldSend) {
                try {
                    List<User> familyMembers = userJpaRepo.findByFamilyCodeWithFcmToken(dDay.getFamilyCode());

                    if (familyMembers.isEmpty()) {
                        log.warn("디데이 알림 대상 가족 구성원이 없음 - 이벤트: {}", dDay.getTitle());
                        continue;
                    }

                    log.info("알림 전송 대상 가족 구성원 수: {} - 이벤트: {}", familyMembers.size(), dDay.getTitle());

                    int successCount = 0;
                    for (User user : familyMembers) {
                        if (user.getFcmToken() != null && !user.getFcmToken().trim().isEmpty()) {
                            log.info("푸시 알림 전송 시도 - 사용자: {}, 이벤트: {}", user.getUsername(), dDay.getTitle());
                            pushNotificationService.sendDdayReminderNotification(
                                    user.getFcmToken(),
                                    dDay.getTitle()
                            );
                            successCount++;
                        } else {
                            log.warn("FCM 토큰이 없는 사용자 - ID: {}, 이름: {}", user.getId(), user.getUsername());
                        }
                    }

                    dDay.markNotificationSent();
                    dDayJpaRepo.save(dDay);
                    log.info("디데이 알림 전송 완료 - 이벤트: {}, 시작시간: {}, 전송 성공: {}/{}",
                            dDay.getTitle(), dDay.getStartTime(), successCount, familyMembers.size());
                } catch (Exception e) {
                    log.error("디데이 알림 전송 중 오류 발생 - 이벤트: {}, 에러: {}",
                            dDay.getTitle(), e.getMessage(), e);
                }
            } else {
                log.info("알림 전송 조건 미충족 - 이벤트: {}, 시작시간: {}, 현재시간: {}",
                        dDay.getTitle(), dDay.getStartTime(), now);
            }
        }
    }

    /**
     * 비활성 사용자 알림 스케줄러 (매일 오전 9시에 실행)
     */
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void sendInactivityNotifications() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<User> inactiveUsers = userJpaRepo.findUsersInactiveForDays(sevenDaysAgo);

        if (inactiveUsers.isEmpty()) {
            log.info("비활성 사용자가 없습니다.");
            return;
        }

        log.info("비활성 사용자 알림 대상: {}명", inactiveUsers.size());

        int successCount = 0;
        for (User user : inactiveUsers) {
            if (user.getFcmToken() != null && !user.getFcmToken().trim().isEmpty()) {
                try {
                    pushNotificationService.sendInactivityNotification(
                            user.getFcmToken(),
                            user.getUsername()
                    );
                    successCount++;
                } catch (Exception e) {
                    log.error("비활성 사용자 알림 전송 실패 - 사용자 ID: {}, 에러: {}",
                            user.getId(), e.getMessage());
                }
            }
        }

        log.info("비활성 사용자 알림 전송 완료 - 성공: {}/{}", successCount, inactiveUsers.size());
    }

    /**
     * 30분 전 알림 발송 여부 판단 (개선된 로직)
     * 5분 간격 체크에 맞춰 ±7분 오차 허용
     */
    private boolean shouldSendNotification(String startTime, LocalDateTime now) {
        if (startTime == null || startTime.trim().isEmpty()) {
            log.warn("시작시간이 설정되지 않음");
            return false;
        }

        try {
            // startTime을 "HH:mm" 형식으로 파싱
            String[] timeParts = startTime.split(":");
            if (timeParts.length != 2) {
                log.warn("잘못된 시간 형식: {}", startTime);
                return false;
            }

            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            // 오늘 날짜의 시작시간으로 LocalDateTime 생성
            LocalDateTime eventDateTime = now.toLocalDate().atTime(hour, minute);

            // 30분 전 시간 계산
            LocalDateTime notificationTime = eventDateTime.minusMinutes(30);

            // 현재 시간이 알림 시간의 ±7분 범위 내에 있는지 확인
            LocalDateTime windowStart = notificationTime.minusMinutes(7);
            LocalDateTime windowEnd = notificationTime.plusMinutes(7);

            boolean shouldSend = !now.isBefore(windowStart) && !now.isAfter(windowEnd);

            log.info("알림 전송 시간 검증 - 이벤트시간: {}, 알림시간: {}, 현재시간: {}, 범위: {} ~ {}, 전송여부: {}",
                    eventDateTime, notificationTime, now, windowStart, windowEnd, shouldSend);

            return shouldSend;

        } catch (NumberFormatException e) {
            log.error("시간 파싱 오류 - startTime: {}, 에러: {}", startTime, e.getMessage());
            return false;
        }
    }
}