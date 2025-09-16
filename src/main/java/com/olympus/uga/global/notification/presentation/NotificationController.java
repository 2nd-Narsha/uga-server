package com.olympus.uga.global.notification.presentation;

import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final UserJpaRepo userJpaRepo;
    private final UserSessionHolder userSessionHolder;

    @PostMapping("/fcm-token")
    public Response updateFcmToken(@RequestBody FcmTokenRequest request) {
        User user = userSessionHolder.getUser();
        user.updateFcmToken(request.fcmToken());
        user.updateLastActivityAt();
        userJpaRepo.save(user);

        return Response.ok("FCM 토큰이 성공적으로 등록되었습니다.");
    }

    @DeleteMapping("/fcm-token")
    public Response deleteFcmToken() {
        User user = userSessionHolder.getUser();
        user.updateFcmToken(null);
        user.updateLastActivityAt();
        userJpaRepo.save(user);

        return Response.ok("FCM 토큰이 삭제되었습니다.");
    }

    public record FcmTokenRequest(String fcmToken) {}
}
