package com.olympus.uga.global.notification.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {
    /**
     * 단일 사용자에게 푸시 알림 전송
     */
    public void sendNotification(String fcmToken, String title, String body, Map<String, String> data) {
        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setAlert(ApsAlert.builder()
                                            .setTitle(title)
                                            .setBody(body)
                                            .build())
                                    .setBadge(1)
                                    .setSound("default")
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("푸시 알림 전송 성공: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("푸시 알림 전송 실패: {}", e.getMessage());
        }
    }

    /**
     * 여러 사용자에게 푸시 알림 전송
     */
    public void sendMulticastNotification(List<String> fcmTokens, String title, String body, Map<String, String> data) {
        try {
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(fcmTokens)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setAlert(ApsAlert.builder()
                                            .setTitle(title)
                                            .setBody(body)
                                            .build())
                                    .setBadge(1)
                                    .setSound("default")
                                    .build())
                            .build())
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            log.info("멀티캐스트 푸시 알림 전송 - 성공: {}, 실패: {}",
                    response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("멀티캐스트 푸시 알림 전송 실패: {}", e.getMessage());
        }
    }

    /**
     * 편지 도착 알림
     */
    public void sendLetterNotification(String fcmToken, String senderName) {
        Map<String, String> data = Map.of(
                "type", "LETTER_RECEIVED",
                "sender", senderName
        );
        sendNotification(fcmToken, "새 편지가 도착했어요! 📮",
                senderName + "님이 편지를 보내셨습니다.", data);
    }

    /**
     * 접속 독려 알림 (7일 미접속)
     */
    public void sendInactivityNotification(String fcmToken, String userName) {
        Map<String, String> data = Map.of(
                "type", "INACTIVITY_REMINDER"
        );
        sendNotification(fcmToken, "우가가 기다리고 있어요! 🐣",
                userName + "님, 가족들이 보고 싶어해요. 어서 놀러오세요!", data);
    }

    /**
     * 디데이 알림 (30분 전)
     */
    public void sendDdayReminderNotification(String fcmToken, String eventName) {
        Map<String, String> data = Map.of(
                "type", "DDAY_REMINDER",
                "eventName", eventName
        );
        sendNotification(fcmToken, "곧 특별한 날이에요! 🎉",
                eventName + "까지 30분 남았습니다.", data);
    }
}
