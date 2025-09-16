package com.olympus.uga.global.websocket.presentation;

import com.olympus.uga.global.websocket.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    /**
     * 클라이언트가 가족 채널에 구독할 때
     */
    @MessageMapping("/family/{familyCode}/join")
    @SendTo("/topic/family/{familyCode}")
    public WebSocketMessage joinFamily(@DestinationVariable String familyCode,
                                     WebSocketMessage message,
                                     SimpMessageHeaderAccessor headerAccessor) {

        String sessionId = headerAccessor.getSessionId();
        log.info("사용자가 가족 채널에 참여: familyCode={}, sessionId={}", familyCode, sessionId);

        return WebSocketMessage.builder()
                .type(WebSocketMessage.MessageType.FAMILY_ACTIVITY)
                .familyCode(familyCode)
                .data("사용자가 참여했습니다.")
                .build();
    }

    /**
     * 클라이언트가 개인 채널에 구독할 때
     */
    @MessageMapping("/user/{userId}/join")
    public void joinUser(@DestinationVariable Long userId,
                        SimpMessageHeaderAccessor headerAccessor) {

        String sessionId = headerAccessor.getSessionId();
        log.info("사용자가 개인 채널에 참여: userId={}, sessionId={}", userId, sessionId);
    }
}
