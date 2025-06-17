package com.olympus.uga.domain.calendar.presentation.dto.request;

import java.time.LocalDate;
import java.util.List;

public record ScheduleUpdateReq(
        Long scheduleId,
        String title,
        LocalDate date,
        String startTime,
        String endTime,
        List<Long> participantIds) {
}
