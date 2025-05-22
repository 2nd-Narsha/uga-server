package com.olympus.uga.domain.user.presentation;

import com.olympus.uga.domain.user.presentation.dto.response.UserResponse;
import com.olympus.uga.domain.user.service.UserService;
import com.olympus.uga.global.common.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseData<UserResponse> getMe() {
        return userService.getMe();
    }
}
