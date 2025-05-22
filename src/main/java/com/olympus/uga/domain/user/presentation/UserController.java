package com.olympus.uga.domain.user.presentation;

import com.olympus.uga.domain.user.presentation.dto.response.UserResponse;
import com.olympus.uga.domain.user.service.UserService;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "마이페이지 정보")
    public ResponseData<UserResponse> getMe() {
        return userService.getMe();
    }

    @DeleteMapping("/delete")
    @Operation(summary = "회원탈퇴")
    public Response deleteUser() {
        return userService.deleteUser();
    }
}
