package com.olympus.uga.global.security.auth;

import com.olympus.uga.domain.auth.error.AuthErrorCode;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSessionHolder{
    private final UserJpaRepo userJpaRepo;

    public User getUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof AuthDetails authDetails) {
            return authDetails.getUser();
        } else {
            throw new CustomException(AuthErrorCode.USER_NOT_FOUND);
        }
    }
}
