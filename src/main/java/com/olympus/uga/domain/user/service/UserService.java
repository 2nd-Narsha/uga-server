package com.olympus.uga.domain.user.service;

import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.presentation.dto.response.UserResponse;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import com.olympus.uga.global.security.jwt.service.JwtTokenService;
import com.olympus.uga.global.security.jwt.util.JwtExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserSessionHolder userSessionHolder;
    private final UserJpaRepo userJpaRepo;
    private JwtExtractor jwtExtractor;
    private JwtTokenService jwtTokenService;

    @Transactional
    public ResponseData<UserResponse> getMe() {
        User user = userSessionHolder.getUser();

        return ResponseData.ok("사용자 정보를 성공적으로 가져왔습니다.", UserResponse.getUsername(user));
    }

    public Response logout(HttpServletRequest req) {
        try {
            String token = jwtExtractor.getToken(req);

            if (token != null) {
                jwtTokenService.addToBlacklist(token);
            }

            SecurityContextHolder.clearContext();

            return Response.ok("로그아웃에 성공하였습니다.");
        } catch (Exception e) { // 토큰이 유효하지 않아도 로그아웃은 성공으로 처리
            SecurityContextHolder.clearContext();
            return Response.ok("로그아웃에 성공하였습니다.");
        }
    }

    public Response deleteUser() {
        User user = userSessionHolder.getUser();

        userJpaRepo.deleteById(user.getId());

        return Response.ok("회원탈퇴에 성공하였습니다.");
    }
}
