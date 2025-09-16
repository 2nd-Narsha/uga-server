package com.olympus.uga.domain.calendar.presentation.dto.request;

import java.time.LocalDate;

public record DDayUpdateReq(
    Long id,
    String title,
    LocalDate date,
    Boolean isHighlight,
    String startTime,
    String endTime
) {
}
