package com.olympus.uga.global.websocket.service;

import com.olympus.uga.global.websocket.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 가족 전체에게 메시지 전송
     */
    public void sendToFamily(String familyCode, WebSocketMessage.MessageType type, Object data) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(type)
                .familyCode(familyCode)
                .data(data)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        String destination = "/topic/family/" + familyCode;
        messagingTemplate.convertAndSend(destination, message);

        log.info("웹소켓 메시지 전송 - 가족: {}, 타입: {}", familyCode, type);
    }

    /**
     * 특정 사용자에게 메시지 전송
     */
    public void sendToUser(Long userId, WebSocketMessage.MessageType type, Object data) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(type)
                .userId(userId)
                .data(data)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();

        String destination = "/queue/user/" + userId;
        messagingTemplate.convertAndSend(destination, message);

        log.info("웹소켓 메시지 전송 - 사용자: {}, 타입: {}", userId, type);
    }

    /**
     * 포인트 변경 알림
     */
    public void notifyPointUpdate(String familyCode, Long userId, Integer newPoints, String reason) {
        PointUpdateData data = new PointUpdateData(userId, newPoints, reason);
        sendToFamily(familyCode, WebSocketMessage.MessageType.POINT_UPDATE, data);
    }

    /**
     * 우가 성장도 변경 알림
     */
    public void notifyUgaGrowthUpdate(String familyCode, Object ugaData) {
        sendToFamily(familyCode, WebSocketMessage.MessageType.UGA_GROWTH_UPDATE, ugaData);
    }

    /**
     * 기여도 변경 알림
     */
    public void notifyContributionUpdate(String familyCode, Long userId, Object contributionData) {
        ContributionUpdateData data = new ContributionUpdateData(userId, contributionData);
        sendToFamily(familyCode, WebSocketMessage.MessageType.CONTRIBUTION_UPDATE, data);
    }

    /**
     * 메모 업데이트 알림
     */
    public void notifyMemoUpdate(String familyCode, Object memoData) {
        sendToFamily(familyCode, WebSocketMessage.MessageType.MEMO_UPDATE, memoData);
    }

    /**
     * 편지 도착 알림
     */
    public void notifyLetterReceived(Long userId, Object letterData) {
        sendToUser(userId, WebSocketMessage.MessageType.LETTER_RECEIVED, letterData);
    }

    // 내부 데이터 클래스들
    public record PointUpdateData(Long userId, Integer newPoints, String reason) {}
    public record ContributionUpdateData(Long userId, Object contributionData) {}
}
