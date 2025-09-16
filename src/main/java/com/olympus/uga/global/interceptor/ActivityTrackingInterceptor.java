package com.olympus.uga.global.interceptor;

import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityTrackingInterceptor implements HandlerInterceptor {
    private final UserSessionHolder userSessionHolder;
    private final UserJpaRepo userJpaRepo;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // 인증된 사용자의 활동 시간 업데이트
            User user = userSessionHolder.getUser();
            if (user != null) {
                user.updateLastActivityAt();
                userJpaRepo.save(user);
            }
        } catch (Exception e) {
            // 인증되지 않은 사용자이거나 오류가 있는 경우 무시
            log.debug("활동 시간 업데이트 실패: {}", e.getMessage());
        }
        return true;
    }
}
