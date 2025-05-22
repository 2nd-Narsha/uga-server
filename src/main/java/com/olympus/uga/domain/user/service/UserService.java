package com.olympus.uga.domain.user.service;

import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.presentation.dto.response.UserResponse;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserSessionHolder userSessionHolder;
    private final UserJpaRepo userJpaRepo;

    @Transactional
    public ResponseData<UserResponse> getMe() {
        User user = userSessionHolder.getUser();

        return ResponseData.ok("사용자 정보를 성공적으로 가져왔습니다.", UserResponse.getUsername(user));
    }

    public Response deleteUser() {
        User user = userSessionHolder.getUser();

        userJpaRepo.deleteById(user.getId());

        return Response.ok("회원탈퇴에 성공하였습니다.");
    }
}
