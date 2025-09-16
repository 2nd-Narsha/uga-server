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

    // 단일 사용자에게 푸시 알림 전송
    public void sendNotification(String fcmToken, String title, String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            log.warn("FCM 토큰이 없어 알림을 보낼 수 없습니다.");
            return;
        }

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
            handleFirebaseMessagingException(e, fcmToken);
        } catch (Exception e) {
            log.error("푸시 알림 전송 중 예외 발생: {}", e.getMessage(), e);
        }
    }

    // 여러 사용자에게 푸시 알림 전송
    public void sendMulticastNotification(List<String> fcmTokens, String title, String body, Map<String, String> data) {
        if (fcmTokens == null || fcmTokens.isEmpty()) {
            log.warn("FCM 토큰 목록이 비어있어 알림을 보낼 수 없습니다.");
            return;
        }

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
            log.error("멀티캐스트 푸시 알림 전송 실패: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("멀티캐스트 푸시 알림 전송 중 예외 발생: {}", e.getMessage(), e);
        }
    }

    // 편지 도착 알림
    public void sendLetterNotification(String fcmToken, String senderName) {
        Map<String, String> data = Map.of(
                "type", "LETTER_RECEIVED",
                "sender", senderName
        );
        sendNotification(fcmToken, "새 편지가 도착했어요! 💌",
                senderName + "님이 편지를 보내셨습니다.", data);
    }

    // 접속 독려 알림 (7일 미접속)
    public void sendInactivityNotification(String fcmToken, String userName) {
        Map<String, String> data = Map.of("type", "INACTIVITY_REMINDER");
        sendNotification(fcmToken, "우가가 기다리고 있어요! 🥺",
                userName + "님, 가족들이 보고 싶어해요. 어서 놀러오세요!", data);
    }


    // 디데이 알림 (30분 전)
    public void sendDdayReminderNotification(String fcmToken, String eventName) {
        Map<String, String> data = Map.of(
                "type", "DDAY_REMINDER",
                "eventName", eventName
        );
        sendNotification(fcmToken, "곧 특별한 날이에요! ✨",
                eventName + "까지 30분 남았습니다.", data);
    }

    // 메모 추가 알림
    public void sendMemoAddedNotification(String fcmToken, String authorName) {
        Map<String, String> data = Map.of(
                "type", "MEMO_ADDED",
                "author", authorName
        );
        sendNotification(fcmToken, "새로운 메모가 추가되었어요! 📝",
                authorName + "님이 메모를 업데이트했습니다.", data);
    }

    // 디데이 추가 알림
    public void sendDdayAddedNotification(String fcmToken, String writerName, String ddayTitle) {
        Map<String, String> data = Map.of(
                "type", "DDAY_ADDED",
                "author", writerName,
                "ddayTitle", ddayTitle
        );
        sendNotification(fcmToken, "새로운 디데이가 추가되었어요! 🎯",
                writerName + "님이 '" + ddayTitle + "' 디데이를 추가했습니다.", data);
    }

    // 스케줄 추가 알림
    public void sendScheduleAddedNotification(String fcmToken, String writerName, String scheduleTitle) {
        Map<String, String> data = Map.of(
                "type", "SCHEDULE_ADDED",
                "author", writerName,
                "scheduleTitle", scheduleTitle
        );
        sendNotification(fcmToken, "새로운 일정이 추가되었어요! 📅",
                writerName + "님이 '" + scheduleTitle + "' 일정을 추가했습니다.", data);
    }

    // 우가 성장 단계별 알림
    public void sendUgaGrowthNotification(String fcmToken, int currentLevel, String ugaName) {
        String growthMessage = getGrowthMessage(currentLevel);
        Map<String, String> data = Map.of(
                "type", "UGA_GROWTH",
                "level", String.valueOf(currentLevel),
                "ugaName", ugaName
        );
        sendNotification(fcmToken, "우가가 성장했어요! 🌱✨",
                ugaName + "가 " + growthMessage, data);
    }

    // 성장 단계별 메시지 반환
    private String getGrowthMessage(int level) {
        return switch (level) {
            case 1 -> "아기 우가로 첫 성장했어요!";
            case 2 -> "어린이 우가로 자랐어요!";
            case 3 -> "청소년 우가로 성장했어요!";
            case 4 -> "어른 우가로 성장했어요!";
            case 5 -> "완전히 자란 우가가 되었어요!";
            case 6 -> "우가가 독립했어요!, 우가 사전에서 확인하세요!";
            default -> "계속해서 성장하고 있어요!";
        };
    }

    // Firebase 메시징 예외 처리
    private void handleFirebaseMessagingException(FirebaseMessagingException e, String fcmToken) {
        String errorCode = e.getErrorCode().name();
        switch (errorCode) {
            case "UNREGISTERED":
            case "INVALID_REGISTRATION":
                log.warn("유효하지 않은 FCM 토큰: {}", fcmToken);
                // TODO: 유효하지 않은 토큰을 DB에서 제거하는 로직 추가
                break;
            case "QUOTA_EXCEEDED":
                log.error("FCM 할당량 초과");
                break;
            case "SENDER_ID_MISMATCH":
                log.error("잘못된 발신자 ID");
                break;
            default:
                log.error("FCM 전송 실패 - 코드: {}, 메시지: {}", errorCode, e.getMessage());
        }
    }
}