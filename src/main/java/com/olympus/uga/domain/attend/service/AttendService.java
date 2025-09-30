package com.olympus.uga.domain.attend.service;
import com.olympus.uga.domain.attend.domain.Attend;
import com.olympus.uga.domain.attend.domain.repo.AttendJpaRepo;
import com.olympus.uga.domain.attend.error.AttendErrorCode;
import com.olympus.uga.domain.attend.presentation.dto.response.AttendStatusRes;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import com.olympus.uga.global.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Slf4j
@Service
@RequiredArgsConstructor
public class AttendService {
    private final AttendJpaRepo attendJpaRepo;
    private final UserJpaRepo userJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final WebSocketService webSocketService;

    public AttendStatusRes getAttendStatus() {
        User user = userSessionHolder.getUser();
        Attend attend = getOrCreateAttend(user);

        LocalDate today = LocalDate.now();
        boolean canAttendToday = canAttendToday(attend, today);

        // 연속성 체크 및 필요시 초기화
        if (shouldResetStreak(attend, today)) {
            attend.resetStreak();
            attendJpaRepo.save(attend);
        }

        return AttendStatusRes.from(attend, canAttendToday);
    }

    @Transactional
    public Response checkAttend() {
//        User user = userSessionHolder.getUser();
//        LocalDate today = LocalDate.now();
//
//        Attend attend = getOrCreateAttend(user);
//
//        // 오늘 출석 여부 확인
//        if (!canAttendToday(attend, today)) {
//            throw new CustomException(AttendErrorCode.ALREADY_CHECKED_TODAY);
//        }
//
//        // 연속성 체크 및 필요시 초기화
//        if (shouldResetStreak(attend, today)) {
//            attend.resetStreak();
//        }
//
//        // 연속 출석일수 계산
//        int newStreak = attend.getCurrentStreak() + 1;
//
//        // 포인트 계산 (1-6일: 3포인트, 7일: 7포인트)
//        int points = (newStreak == 7) ? 7 : 3;
//
//        // 출석 정보 업데이트
//        attend.updateAttend(today, newStreak);
//
//        // 7일 달성시 초기화
//        if (newStreak == 7) {
//            attend.resetStreak();
//        }
//
//        attendJpaRepo.save(attend);

        User user = userSessionHolder.getUser();
        LocalDate today = LocalDate.now();

        Attend attend = getOrCreateAttend(user);

        log.info("=== 출석체크 시작 ===");
        log.info("userId: {}", user.getId());
        log.info("출석 전 currentStreak: {}", attend.getCurrentStreak());
        log.info("출석 전 lastAttendDate: {}", attend.getLastAttendDate());

        // 오늘 출석 여부 확인
        if (!canAttendToday(attend, today)) {
            throw new CustomException(AttendErrorCode.ALREADY_CHECKED_TODAY);
        }

        // 연속성 체크 및 필요시 초기화
        if (shouldResetStreak(attend, today)) {
            log.info("연속 출석 초기화!");
            attend.resetStreak();
        }

        // 연속 출석일수 계산
        int newStreak = attend.getCurrentStreak() + 1;
        log.info("새로운 streak: {}", newStreak);

        // 포인트 계산 (1-6일: 3포인트, 7일: 7포인트)
        int points = (newStreak == 7) ? 7 : 3;

        // 출석 정보 업데이트
        attend.updateAttend(today, newStreak);
        log.info("updateAttend 호출 후 currentStreak: {}", attend.getCurrentStreak());

        // 7일 달성시 초기화
        if (newStreak == 7) {
            attend.resetStreak();
        }

        Attend savedAttend = attendJpaRepo.save(attend);
        log.info("save 후 currentStreak: {}", savedAttend.getCurrentStreak());

        user.earnPoint(points);
        user.updateLastActivityAt();

        userJpaRepo.save(user);

        webSocketService.notifyPointUpdate(user.getId(), points, "ATTEND_CHECK");

        String message = (newStreak == 7)
                ? "7일 연속 출석 완료! " + points + "포인트를 획득했습니다. 출석 카운터가 초기화됩니다."
                : "출석체크 완료! (연속 " + newStreak + "일)" + points + "포인트를 획득했습니다.";

        return Response.ok(message);
    }

    private Attend getOrCreateAttend(User user) {
        return attendJpaRepo.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Attend newAttend = Attend.builder()
                            .user(user)
                            .currentStreak(0)
                            .lastAttendDate(LocalDate.now().minusDays(1)) // 어제로 설정하여 오늘 출석 가능하게 함
                            .build();
                    return attendJpaRepo.save(newAttend);
                });
    }

    private boolean canAttendToday(Attend attend, LocalDate today) {
        return !attend.getLastAttendDate().equals(today); // 마지막 출석일이 오늘이 아니면 출석 가능
    }

    private boolean shouldResetStreak(Attend attend, LocalDate today) {
        LocalDate lastAttendDate = attend.getLastAttendDate();
        // 연속성 체크: 마지막 출석일이 어제가 아니고, 현재 연속일수가 0보다 크면 초기화
        return !lastAttendDate.equals(today.minusDays(1)) && attend.getCurrentStreak() > 0;
    }
}
