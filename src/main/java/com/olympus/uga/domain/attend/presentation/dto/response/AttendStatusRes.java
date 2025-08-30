package com.olympus.uga.domain.attend.presentation.dto.response;

import com.olympus.uga.domain.attend.domain.Attend;

import java.time.LocalDate;

public record AttendStatusRes(Integer currentStreak,
                              LocalDate lastAttendDate,
                              Boolean canAttendToday) { // 오늘 출석 가능 여부
    public static AttendStatusRes from(Attend attend, Boolean canAttendToday) {
        return new AttendStatusRes(
                attend.getCurrentStreak(),
                attend.getLastAttendDate(),
                canAttendToday
        );
    }
}