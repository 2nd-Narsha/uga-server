package com.olympus.uga.global.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private MessageType type;
    private String familyCode;
    private Long userId;
    private Object data;
    private String timestamp;

    public enum MessageType {
        POINT_UPDATE,           // 포인트 변경
        UGA_GROWTH_UPDATE,      // 우가 성장도 변경
        CONTRIBUTION_UPDATE,    // 내 기여도 변경
        MEMO_UPDATE,           // 메모 업데이트
        LETTER_RECEIVED,       // 편지 도착
        ATTENDANCE_CHECK,      // 출석 체크
        FAMILY_ACTIVITY,       // 가족 활동 알림
        SYSTEM,
        JOIN_FAMILY,
        JOIN_USER,
        LEAVE_FAMILY,
        ERROR
    }
}
