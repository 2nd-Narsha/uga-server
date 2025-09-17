package com.olympus.uga.domain.calendar.presentation.dto.response;

import com.olympus.uga.domain.calendar.domain.DDay;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record DDayListRes(Long ddayId, String title, String dday, Boolean isHighlight) {
    public static DDayListRes from(DDay dday) {
        return new DDayListRes(
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
            return "D-DAY";
        } else if (daysBetween > 0) {
            return "D-" + daysBetween;
        } else {
            return "D+" + Math.abs(daysBetween);
        }
    }
}
