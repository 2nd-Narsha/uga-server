package com.olympus.uga.domain.calendar.presentation.dto.request;

import java.time.LocalDate;

public record DdayUpdateReq(Long ddayId, String title, LocalDate date, Boolean isHighlight) {
}
