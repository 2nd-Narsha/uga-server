package com.olympus.uga.domain.calendar.presentation.dto.response;

import com.olympus.uga.domain.calendar.domain.Dday;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record DdayListRes(Long ddayId, String title, String dday, Boolean isHighlight) {
    public static DdayListRes from(Dday dday) {
        return new DdayListRes(
                dday.getId(),
                dday.getTitle(),
                calculateDday(dday.getDate()),
                dday.getIsHighlight()
        );
    }

    private static String calculateDday(LocalDate targetDate) {
        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(today, targetDate);

        if (daysBetween == 0) {
            return "D-Day";
        } else if (daysBetween > 0) {
            return "D-" + daysBetween;
        } else {
            return "D+" + Math.abs(daysBetween);
        }
    }
}
