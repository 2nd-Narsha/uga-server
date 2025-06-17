package com.olympus.uga.domain.calendar.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CalendarErrorCode implements CustomErrorCode {
    DDAY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 디데이를 찾을 수 없습니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 일정을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}