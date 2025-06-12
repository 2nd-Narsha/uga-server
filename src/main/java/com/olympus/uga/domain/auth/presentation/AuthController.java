package com.olympus.uga.domain.auth.presentation;

import com.olympus.uga.domain.auth.presentation.dto.request.RefreshReq;
import com.olympus.uga.domain.auth.presentation.dto.request.SignInReq;
import com.olympus.uga.domain.auth.presentation.dto.request.SignUpReq;
import com.olympus.uga.domain.auth.presentation.dto.response.RefreshRes;
import com.olympus.uga.domain.auth.presentation.dto.response.SignInRes;
import com.olympus.uga.domain.auth.usecase.AuthUseCase;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthUseCase authUseCase;

    @PostMapping("/sign-up")
    @Operation(summary = "회원가입", description = "회원가입 전 인증코드 먼저 수행하기, 전화번호 입력 시 '-' 없이 입력")
    public Response signUp(@RequestBody SignUpReq req) {
        return authUseCase.signUp(req);
    }

    @PostMapping("/sign-in")
    @Operation(summary = "로그인", description = "리프레쉬 토큰: 1주, 액세스 토큰: 2시간")
    public ResponseData<SignInRes> signIn(@Validated @RequestBody SignInReq req) {
        return authUseCase.signIn(req);
    }

    @PostMapping("/refresh")
    @Operation(summary = "액세스 토큰 재발급")
    public ResponseData<RefreshRes> refresh(@Validated @RequestBody RefreshReq req) {
        return authUseCase.refresh(req);
    }
}
