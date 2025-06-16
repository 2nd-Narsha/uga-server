package com.olympus.uga.domain.user.presentation;

import com.olympus.uga.domain.user.presentation.dto.response.UserResponse;
import com.olympus.uga.domain.user.service.UserService;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PatchMapping(value = "/update/profileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 이미지 변경")
    public Response updateProfile(@RequestPart(name = "profileImage") MultipartFile profileImage) {
        return userService.updateProfile(profileImage);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "토큰 재사용 불가, 다시 로그인")
    public Response logout(HttpServletRequest req) {
        return userService.logout(req);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "회원탈퇴")
    public Response deleteUser() {
        return userService.deleteUser();
    }
}
