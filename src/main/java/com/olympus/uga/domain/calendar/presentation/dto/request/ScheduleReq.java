package com.olympus.uga.domain.calendar.presentation.dto.request;

import com.olympus.uga.domain.calendar.domain.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ScheduleReq(
        String title,
        LocalDate date,
        String startTime,
        String endTime,
        List<Long> participantIds // 참여자 ID 리스트
) {
    public static Schedule fromScheduleReq(String familyCode, ScheduleReq req) {
        return Schedule.builder()
                .title(req.title)
                .date(req.date)
                .startTime(req.startTime)
                .endTime(req.endTime)
                .familyCode(familyCode)
                .build();
    }
}
