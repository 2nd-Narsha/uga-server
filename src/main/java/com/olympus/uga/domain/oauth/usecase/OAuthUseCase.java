package com.olympus.uga.domain.oauth.usecase;

import com.olympus.uga.domain.auth.presentation.dto.response.SignInRes;
import com.olympus.uga.domain.oauth.presentation.dto.response.GoogleUserInfoDto;
import com.olympus.uga.domain.oauth.presentation.dto.response.KakaoUserInfoDto;
import com.olympus.uga.domain.oauth.service.GoogleOAuthService;
import com.olympus.uga.domain.oauth.service.KakaoOAuthService;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.enums.LoginType;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.ResponseData;
import com.olympus.uga.global.security.jwt.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static com.olympus.uga.domain.oauth.presentation.dto.response.GoogleUserInfoDto.registerGoogleUser;
import static com.olympus.uga.domain.oauth.presentation.dto.response.KakaoUserInfoDto.registerKakaoUser;

@Service
@RequiredArgsConstructor
public class OAuthUseCase {
    private final UserJpaRepo userJpaRepo;
    private final KakaoOAuthService kakaoOAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final JwtProvider jwtProvider;

    @Transactional
    public ResponseData<SignInRes> loginWithKakaoCode(String code) {
        String encoded = URLDecoder.decode(code, StandardCharsets.UTF_8);
        String accessToken = kakaoOAuthService.getAccessToken(encoded);

        KakaoUserInfoDto userInfo = kakaoOAuthService.getUserInfo(accessToken);

        User user = userJpaRepo.findByOauthIdAndLoginType(userInfo.id(), LoginType.KAKAO)
                .orElseGet(() -> {
                    User newUser = registerKakaoUser(userInfo);
                    return userJpaRepo.save(newUser);
                });

        return ResponseData.ok("카카오 로그인에 성공하였습니다.", jwtProvider.createToken(user.getId()));
    }

    @Transactional
    public ResponseData<SignInRes> loginWithGoogleCode(String code) {
        String encoded = URLDecoder.decode(code, StandardCharsets.UTF_8);
        String accessToken = googleOAuthService.getAccessToken(encoded);

        GoogleUserInfoDto userInfo = googleOAuthService.getUserInfo(accessToken);

        User user = userJpaRepo.findByOauthIdAndLoginType(userInfo.id(), LoginType.GOOGLE)
                .orElseGet(() -> {
                    User newUser = registerGoogleUser(userInfo);
                    return userJpaRepo.save(newUser);
                });

        return ResponseData.ok("구글 로그인에 성공하였습니다.", jwtProvider.createToken(user.getId()));
    }
}