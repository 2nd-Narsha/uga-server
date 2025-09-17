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

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityTrackingInterceptor implements HandlerInterceptor {
    private final UserSessionHolder userSessionHolder;
    private final UserJpaRepo userJpaRepo;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            User user = userSessionHolder.getUser();
            if (user != null) {
                user.updateLastActivityAt();
                userJpaRepo.save(user);
            }
        } catch (Exception e) {
            log.debug("활동 시간 업데이트 실패: {}", e.getMessage());
        }
        return true;
    }
}
