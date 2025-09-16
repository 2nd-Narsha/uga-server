package com.olympus.uga.domain.calendar.presentation.dto.request;

import com.olympus.uga.domain.calendar.domain.DDay;

import java.time.LocalDate;

public record DDayReq(
    String title,
    LocalDate date,
    Boolean isHighlight,
    String startTime,
    String endTime
) {
    public static DDay fromDdayReq(String familyCode, DDayReq req) {
        return DDay.builder()
                .title(req.title())
                .date(req.date())
                .isHighlight(req.isHighlight())
                .startTime(req.startTime())
                .endTime(req.endTime())
                .familyCode(familyCode)
                .build();
    }
}