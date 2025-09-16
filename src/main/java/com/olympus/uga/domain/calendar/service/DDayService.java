package com.olympus.uga.domain.calendar.service;

import com.olympus.uga.domain.calendar.domain.DDay;
import com.olympus.uga.domain.calendar.domain.repo.DDayJpaRepo;
import com.olympus.uga.domain.calendar.error.CalendarErrorCode;
import com.olympus.uga.domain.calendar.presentation.dto.request.DDayReq;
import com.olympus.uga.domain.calendar.presentation.dto.request.DDayUpdateReq;
import com.olympus.uga.domain.calendar.presentation.dto.response.DDayListRes;
import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.notification.service.PushNotificationService;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DDayService {
    private final DDayJpaRepo dDayJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final UserJpaRepo userJpaRepo;
    private final FamilyJpaRepo familyJpaRepo;
    private final PushNotificationService pushNotificationService;

    @Transactional(readOnly = true)
    public List<DDayListRes> getList() {
        User user = userSessionHolder.getUser();
        List<DDay> ddays = dDayJpaRepo.findByFamilyCodeOrderByDateAsc(user.getFamilyCode());

        return ddays.stream()
                .map(DDayListRes::from)
                .toList();
    }

    @Transactional
    public Response createDday(DDayReq req) {
        User user = userSessionHolder.getUser();
        user.updateLastActivityAt();
        userJpaRepo.save(user);

        dDayJpaRepo.save(DDayReq.fromDdayReq(user.getFamilyCode(), req));

        // 가족들에게 일정 추가 푸시 알림 전송 (자신 제외)
        sendScheduleAddedNotification(user, req.title());

        return Response.created("디데이가 생성되었습니다.");
    }

    @Transactional
    public Response updateDday(DDayUpdateReq req) {
        User user = userSessionHolder.getUser();
        user.updateLastActivityAt();

        DDay dday = dDayJpaRepo.findById(req.id())
                .orElseThrow(() -> new CustomException(CalendarErrorCode.DDAY_NOT_FOUND));

        if (!dday.getFamilyCode().equals(user.getFamilyCode())) {
            throw new CustomException(CalendarErrorCode.CAN_NOT_UPDATE);
        }

        dday.updateDday(req.title(), req.date(), req.startTime(), req.endTime(), req.isHighlight());
        userJpaRepo.save(user);

        return Response.ok("디데이가 수정되었습니다.");
    }

    @Transactional
    public Response deleteDday(Long ddayId) {
        User user = userSessionHolder.getUser();
        user.updateLastActivityAt();

        DDay dday = dDayJpaRepo.findById(ddayId)
                .orElseThrow(() -> new CustomException(CalendarErrorCode.DDAY_NOT_FOUND));

        if (!dday.getFamilyCode().equals(user.getFamilyCode())) {
            throw new CustomException(CalendarErrorCode.CAN_NOT_DELETE);
        }

        dDayJpaRepo.delete(dday);
        userJpaRepo.save(user);

        return Response.ok("디데이가 삭제되었습니다.");
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void deleteExpiredDdays() {
        LocalDate today = LocalDate.now();
        List<DDay> expiredDdays = dDayJpaRepo.findByDateBefore(today);

        if (!expiredDdays.isEmpty()) {
            dDayJpaRepo.deleteAll(expiredDdays);
            log.info("만료된 디데이 {}개를 삭제했습니다.", expiredDdays.size());
        }
    }

    // 일정 추가 시 가족들에게 푸시 알림 전송
    private void sendScheduleAddedNotification(User writer, String scheduleTitle) {
        try {
            Family family = familyJpaRepo.findByMemberListContaining(writer.getId())
                    .orElse(null);

            if (family == null) {
                return;
            }

            // 가족 구성원들의 FCM 토큰 가져오기 (작성자 제외)
            List<User> familyMembers = userJpaRepo.findAllById(family.getMemberList());

            for (User member : familyMembers) {
                if (!member.getId().equals(writer.getId()) && member.getFcmToken() != null) {
                    pushNotificationService.sendDdayAddedNotification(
                        member.getFcmToken(),
                        writer.getUsername(),
                        scheduleTitle
                    );
                }
            }
        } catch (Exception e) {
            // 푸시 알림 실패해도 일정 저장은 성공으로 처리
            log.warn("디데이 추가 푸시 알림 전송 실패: {}", e.getMessage());
        }
    }
}
