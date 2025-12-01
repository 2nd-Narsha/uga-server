package com.olympus.uga.global.websocket;

import com.olympus.uga.global.websocket.handler.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    // ========== STOMP 설정 (기존 웹 클라이언트용) ==========
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // SockJS 사용 (웹 브라우저용)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // 순수 WebSocket + STOMP (Postman용 - SockJS 없이)
        registry.addEndpoint("/ws-stomp")  // 🔥 경로 변경 (/ws/stomp → /ws-stomp)
                .setAllowedOriginPatterns("*");
    }

    // ========== 순수 WebSocket 설정 (React Native용) ==========
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws-native")  // 🔥 경로 변경 (/ws/native → /ws-native)
                .setAllowedOriginPatterns("*");
    }
}