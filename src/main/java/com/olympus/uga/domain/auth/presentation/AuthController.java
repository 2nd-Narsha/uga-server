package com.olympus.uga.domain.auth.presentation;

import com.olympus.uga.domain.auth.presentation.dto.request.RefreshReq;
import com.olympus.uga.domain.auth.presentation.dto.request.SignInReq;
import com.olympus.uga.domain.auth.presentation.dto.request.SignUpReq;
import com.olympus.uga.domain.auth.presentation.dto.response.RefreshRes;
import com.olympus.uga.domain.auth.presentation.dto.response.SignInRes;
import com.olympus.uga.domain.auth.usecase.AuthUseCase;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
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
    public Response signUp(@RequestBody SignUpReq req) {
        return authUseCase.signUp(req);
    }

    @PostMapping("/sign-in")
    public ResponseData<SignInRes> signIn(@Validated @RequestBody SignInReq req) {
        return authUseCase.signIn(req);
    }

    @PostMapping("/refresh")
    public ResponseData<RefreshRes> refresh(@Validated @RequestBody RefreshReq req) {
        return authUseCase.refresh(req);
    }
}
