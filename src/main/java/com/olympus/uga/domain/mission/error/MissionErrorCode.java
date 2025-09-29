package com.olympus.uga.domain.mission.error;

import com.olympus.uga.global.exception.error.CustomErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MissionErrorCode implements CustomErrorCode {
    MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "미션을 찾을 수 없습니다."),
    MISSION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 미션에 접근할 수 없습니다."),
    MISSION_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 완료된 미션입니다."),
    REWARD_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "보상을 받을 수 없는 상태입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
