package com.olympus.uga.domain.calendar.service;

import com.olympus.uga.domain.calendar.domain.Schedule;
import com.olympus.uga.domain.calendar.domain.repo.ScheduleJpaRepo;
import com.olympus.uga.domain.calendar.domain.repo.ScheduleParticipantJpaRepo;
import com.olympus.uga.domain.calendar.error.CalendarErrorCode;
import com.olympus.uga.domain.calendar.presentation.dto.request.ScheduleReq;
import com.olympus.uga.domain.calendar.presentation.dto.request.ScheduleUpdateReq;
import com.olympus.uga.domain.calendar.presentation.dto.response.ScheduleListRes;
import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.notification.service.PushNotificationService;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleJpaRepo scheduleJpaRepo;
    private final FamilyJpaRepo familyJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final ScheduleParticipantJpaRepo scheduleParticipantJpaRepo;
    private final UserJpaRepo userJpaRepo;
    private final PushNotificationService pushNotificationService;

    @Transactional(readOnly = true)
    public List<ScheduleListRes> getList(){
        User user = userSessionHolder.getUser();
        List<Schedule> scheduleList = scheduleJpaRepo.findByFamilyCodeOrderByDateAscStartTimeAsc(user.getFamilyCode());

        return scheduleList.stream()
                .map(this::convertToScheduleListRes)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleListRes> getListByDate(LocalDate date) {
        User user = userSessionHolder.getUser();
        List<Schedule> scheduleList = scheduleJpaRepo.findByFamilyCodeAndDateOrderByStartTimeAsc(user.getFamilyCode(), date);

        return scheduleList.stream()
                .map(this::convertToScheduleListRes)
                .toList();
    }

    @Transactional
    public Response createSchedule(ScheduleReq req) {
        User user = userSessionHolder.getUser();

        if (req.participantIds() != null && !req.participantIds().isEmpty()) {
            validateParticipants(req.participantIds(), user.getFamilyCode());
        }

        Schedule schedule = ScheduleReq.fromScheduleReq(user.getFamilyCode(), req);
        Schedule savedSchedule = scheduleJpaRepo.save(schedule);

        if (req.participantIds() != null && !req.participantIds().isEmpty()) {
            req.participantIds().forEach(savedSchedule::addParticipant);
        }

        // 가족들에게 스케줄 추가 푸시 알림 전송 (자신 제외)
        sendScheduleAddedNotification(user, req.title());

        return Response.created(req.title() + "의 일정을 생성하였습니다.");
    }

    @Transactional
    public Response updateSchedule(ScheduleUpdateReq req) {
        User user = userSessionHolder.getUser();

        Schedule schedule = scheduleJpaRepo.findByIdAndFamilyCode(req.scheduleId(), user.getFamilyCode())
                .orElseThrow(() -> new CustomException(CalendarErrorCode.SCHEDULE_NOT_FOUND));

        // 참여자 유효성 검사
        if (req.participantIds() != null && !req.participantIds().isEmpty()) {
            validateParticipants(req.participantIds(), user.getFamilyCode());
        }

        schedule.updateSchedule(
                req.title(),
                req.date(),
                req.startTime(),
                req.endTime()
        );

        // 기존 참여자 삭제 후 새로 추가
        if (req.participantIds() != null) {
            schedule.clearParticipants();
            if (!req.participantIds().isEmpty()) {
                req.participantIds().forEach(schedule::addParticipant);
            }
        }

        return Response.ok("일정을 수정했습니다.");
    }

    @Transactional
    public Response deleteSchedule(Long scheduleId) {
        User user = userSessionHolder.getUser();

        Schedule schedule = scheduleJpaRepo.findByIdAndFamilyCode(scheduleId, user.getFamilyCode())
                .orElseThrow(() -> new CustomException(CalendarErrorCode.SCHEDULE_NOT_FOUND));

        scheduleJpaRepo.delete(schedule);

        return Response.ok("일정을 삭제했습니다.");
    }

    private ScheduleListRes convertToScheduleListRes(Schedule schedule) {
        List<Long> participantIds = scheduleParticipantJpaRepo.findUserIdsByScheduleId(schedule.getId());
        List<User> participantUsers = userJpaRepo.findAllById(participantIds);

        return ScheduleListRes.from(schedule, participantUsers);
    }

    private void validateParticipants(List<Long> participantIds, String familyCode) {
        Family family = familyJpaRepo.findById(familyCode)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        for (Long participantId : participantIds) {
            if (!family.getMemberList().contains(participantId)) {
                throw new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER);
            }
        }
    }

    // 스케줄 추가 시 가족들에게 푸시 알림 전송 (자신 제외)
    private void sendScheduleAddedNotification(User writer, String scheduleTitle) {
        try {
            Family family = familyJpaRepo.findByMemberListContaining(writer.getId())
                    .orElse(null);

            if (family == null) {
                return;
            }

            List<User> familyMembers = userJpaRepo.findAllById(family.getMemberList());

            for (User member : familyMembers) {
                // 자신은 제외하고 FCM 토큰이 있는 경우에만 전송
                if (!member.getId().equals(writer.getId()) && member.getFcmToken() != null) {
                    pushNotificationService.sendScheduleAddedNotification(
                            member.getFcmToken(),
                            writer.getUsername(),
                            scheduleTitle
                    );
                }
            }
        } catch (Exception e) {
            log.warn("스케줄 추가 푸시 알림 전송 실패: {}", e.getMessage());
        }
    }
}
