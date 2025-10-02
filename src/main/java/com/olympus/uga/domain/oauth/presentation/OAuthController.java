package com.olympus.uga.domain.oauth.presentation;

import com.olympus.uga.domain.auth.presentation.dto.response.SignInRes;
import com.olympus.uga.domain.oauth.service.AppleOAuthService;
import com.olympus.uga.domain.oauth.usecase.OAuthUseCase;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.global.common.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthUseCase oAuthUseCase;

    @PostMapping("/kakao/login")
    @Operation(summary = "카카오 oauth")
    public ResponseData<SignInRes> kakaoLogin(@RequestParam("accessToken") String accessToken) {
        return oAuthUseCase.loginWithKakaoToken(accessToken);
    }

    @PostMapping("/google/login")
    @Operation(summary = "구글 oauth")
    public ResponseData<SignInRes> googleLogin(@RequestParam("accessToken") String accessToken) {
        return oAuthUseCase.loginWithGoogleToken(accessToken);
    }

    @PostMapping("/apple/login")
    @Operation(summary = "애플 oauth")
    public ResponseEntity<ResponseData<SignInRes>> appleRedirect(@RequestParam("code") String code) {
        ResponseData<SignInRes> response = oAuthUseCase.appleLogin(code);
        return ResponseEntity.ok(response);
    }
}