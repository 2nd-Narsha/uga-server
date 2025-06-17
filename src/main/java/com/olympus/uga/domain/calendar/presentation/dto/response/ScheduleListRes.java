package com.olympus.uga.domain.calendar.presentation.dto.response;

import com.olympus.uga.domain.calendar.domain.Schedule;
import com.olympus.uga.domain.user.domain.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ScheduleListRes(
        Long scheduleId,
        String title,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        List<ParticipantInfo> participants
) {
    public static ScheduleListRes from(Schedule schedule, List<User> participantUsers) {
        List<ParticipantInfo> participants = participantUsers.stream()
                .map(user -> new ParticipantInfo(
                        user.getId(),
                        user.getUsername(),
                        user.getProfileImage()
                ))
                .toList();

        return new ScheduleListRes(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                participants
        );
    }
}