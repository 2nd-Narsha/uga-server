package com.olympus.uga.domain.oauth.presentation;

import com.olympus.uga.domain.auth.presentation.dto.response.SignInRes;
import com.olympus.uga.domain.oauth.presentation.dto.request.OAuthLoginReq;
import com.olympus.uga.domain.oauth.usecase.OAuthUseCase;
import com.olympus.uga.global.common.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthUseCase oAuthUseCase;

    @PostMapping("/kakao/login")
    public ResponseData<SignInRes> kakaoLogin(@RequestBody OAuthLoginReq req) {
        return oAuthUseCase.loginWithKakao(req);
    }

    @PostMapping("/google/login")
    public ResponseData<SignInRes> googleLogin(@RequestBody OAuthLoginReq req) {
        return oAuthUseCase.loginWithGoogle(req);

    }
}
