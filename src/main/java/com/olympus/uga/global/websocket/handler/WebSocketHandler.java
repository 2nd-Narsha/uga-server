package com.olympus.uga.global.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olympus.uga.global.websocket.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    // familyCode별로 세션 관리
    private final Map<String, Map<String, WebSocketSession>> familySessions = new ConcurrentHashMap<>();

    // userId별로 세션 관리
    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket 연결 성공: sessionId={}", session.getId());

        // 연결 성공 메시지 전송
        WebSocketMessage message = WebSocketMessage.builder()
                .type(WebSocketMessage.MessageType.SYSTEM)
                .data("연결되었습니다")
                .build();

        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("메시지 수신: sessionId={}, payload={}", session.getId(), payload);

        try {
            WebSocketMessage wsMessage = objectMapper.readValue(payload, WebSocketMessage.class);

            switch (wsMessage.getType()) {
                case JOIN_FAMILY:
                    handleJoinFamily(session, wsMessage);
                    break;
                case JOIN_USER:
                    handleJoinUser(session, wsMessage);
                    break;
                case FAMILY_ACTIVITY:
                    handleFamilyActivity(session, wsMessage);
                    break;
                case LEAVE_FAMILY:
                    handleLeaveFamily(session, wsMessage);
                    break;
                default:
                    log.warn("알 수 없는 메시지 타입: {}", wsMessage.getType());
            }
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생", e);
            sendError(session, "메시지 처리 실패: " + e.getMessage());
        }
    }

    private void handleJoinFamily(WebSocketSession session, WebSocketMessage message) throws IOException {
        String familyCode = message.getFamilyCode();

        familySessions.computeIfAbsent(familyCode, k -> new ConcurrentHashMap<>())
                .put(session.getId(), session);

        log.info("가족 채널 참여: familyCode={}, sessionId={}", familyCode, session.getId());

        // 같은 가족의 모든 세션에 알림
        broadcastToFamily(familyCode, WebSocketMessage.builder()
                .type(WebSocketMessage.MessageType.FAMILY_ACTIVITY)
                .familyCode(familyCode)
                .data("새로운 사용자가 참여했습니다")
                .build());
    }

    private void handleJoinUser(WebSocketSession session, WebSocketMessage message) throws IOException {
        Long userId = message.getUserId();

        userSessions.put(userId, session);

        log.info("개인 채널 참여: userId={}, sessionId={}", userId, session.getId());

        sendToSession(session, WebSocketMessage.builder()
                .type(WebSocketMessage.MessageType.SYSTEM)
                .data("개인 채널에 연결되었습니다")
                .build());
    }

    private void handleFamilyActivity(WebSocketSession session, WebSocketMessage message) throws IOException {
        String familyCode = message.getFamilyCode();

        log.info("가족 활동 메시지: familyCode={}, data={}", familyCode, message.getData());

        // 같은 가족의 모든 세션에 브로드캐스트
        broadcastToFamily(familyCode, message);
    }

    private void handleLeaveFamily(WebSocketSession session, WebSocketMessage message) {
        String familyCode = message.getFamilyCode();

        Map<String, WebSocketSession> sessions = familySessions.get(familyCode);
        if (sessions != null) {
            sessions.remove(session.getId());
            if (sessions.isEmpty()) {
                familySessions.remove(familyCode);
            }
        }

        log.info("가족 채널 나감: familyCode={}, sessionId={}", familyCode, session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket 연결 종료: sessionId={}, status={}", session.getId(), status);

        // 모든 채널에서 세션 제거
        familySessions.values().forEach(sessions -> sessions.remove(session.getId()));
        userSessions.values().remove(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket 에러: sessionId={}", session.getId(), exception);
    }

    // 유틸리티 메서드들

    private void broadcastToFamily(String familyCode, WebSocketMessage message) throws IOException {
        Map<String, WebSocketSession> sessions = familySessions.get(familyCode);
        if (sessions != null) {
            String payload = objectMapper.writeValueAsString(message);
            for (WebSocketSession session : sessions.values()) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            }
        }
    }

    private void sendToSession(WebSocketSession session, WebSocketMessage message) throws IOException {
        if (session.isOpen()) {
            String payload = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(payload));
        }
    }

    private void sendError(WebSocketSession session, String errorMessage) throws IOException {
        WebSocketMessage message = WebSocketMessage.builder()
                .type(WebSocketMessage.MessageType.ERROR)
                .data(errorMessage)
                .build();
        sendToSession(session, message);
    }

    // 외부에서 사용할 수 있는 메서드들

    public void sendToUser(Long userId, WebSocketMessage message) throws IOException {
        WebSocketSession session = userSessions.get(userId);
        if (session != null) {
            sendToSession(session, message);
        }
    }

    public void sendToFamily(String familyCode, WebSocketMessage message) throws IOException {
        broadcastToFamily(familyCode, message);
    }
}