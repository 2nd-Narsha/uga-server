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
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleJpaRepo scheduleJpaRepo;
    private final FamilyJpaRepo familyJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final ScheduleParticipantJpaRepo scheduleParticipantJpaRepo;
    private final UserJpaRepo userJpaRepo;

    public List<ScheduleListRes> getList(){
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        List<Schedule> scheduleList = scheduleJpaRepo.findByFamilyCodeOrderByDateAscStartTimeAsc(userFamilyCode);

        return scheduleList.stream()
                .map(this::convertToScheduleListRes)
                .toList();
    }

    public List<ScheduleListRes> getListByDate(LocalDate date) {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        List<Schedule> scheduleList = scheduleJpaRepo.findByFamilyCodeAndDateOrderByStartTimeAsc(userFamilyCode, date);

        return scheduleList.stream()
                .map(this::convertToScheduleListRes)
                .toList();
    }

    @Transactional
    public Response createSchedule(ScheduleReq req) {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        // 참여자 유효성 검사
        if (req.participantIds() != null && !req.participantIds().isEmpty()) {
            validateParticipants(req.participantIds(), userFamilyCode);
        }

        Schedule schedule = ScheduleReq.fromScheduleReq(userFamilyCode, req);
        Schedule savedSchedule = scheduleJpaRepo.save(schedule);

        // 참여자 추가
        if (req.participantIds() != null && !req.participantIds().isEmpty()) {
            req.participantIds().forEach(savedSchedule::addParticipant);
        }

        return Response.created(req.title() + "의 일정을 생성하였습니다.");
    }

    @Transactional
    public Response updateSchedule(ScheduleUpdateReq req) {
        User user = userSessionHolder.getUser();
        String userFamilyCode = getUserFamilyCode(user.getId());

        Schedule schedule = scheduleJpaRepo.findByIdAndFamilyCode(req.scheduleId(), userFamilyCode)
                .orElseThrow(() -> new CustomException(CalendarErrorCode.SCHEDULE_NOT_FOUND));

        // 참여자 유효성 검사
        if (req.participantIds() != null && !req.participantIds().isEmpty()) {
            validateParticipants(req.participantIds(), userFamilyCode);
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
        String userFamilyCode = getUserFamilyCode(user.getId());

        Schedule schedule = scheduleJpaRepo.findByIdAndFamilyCode(scheduleId, userFamilyCode)
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

    private String getUserFamilyCode(Long userId) {
        Family family = familyJpaRepo.findByMemberListContaining(userId)
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        return family.getFamilyCode();
    }
}
