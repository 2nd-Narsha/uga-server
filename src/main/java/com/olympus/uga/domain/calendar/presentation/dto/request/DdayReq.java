package com.olympus.uga.domain.calendar.presentation.dto.request;

import com.olympus.uga.domain.calendar.domain.Dday;

import java.time.LocalDate;

public record DdayReq(String title, LocalDate date, Boolean isHighlight) {
    public static Dday fromDdayReq(String familyCode, DdayReq req) {
        return Dday.builder()
                .title(req.title)
                .date(req.date)
                .isHighlight(req.isHighlight)
                .familyCode(familyCode)
                .build();
    }
}
