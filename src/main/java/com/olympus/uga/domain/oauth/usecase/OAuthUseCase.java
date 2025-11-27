package com.olympus.uga.domain.oauth.usecase;

import com.olympus.uga.domain.auth.presentation.dto.response.SignInRes;
import com.olympus.uga.domain.oauth.presentation.dto.response.GoogleUserInfoDto;
import com.olympus.uga.domain.oauth.presentation.dto.response.KakaoUserInfoDto;
import com.olympus.uga.domain.oauth.service.AppleOAuthService;
import com.olympus.uga.domain.oauth.service.GoogleOAuthService;
import com.olympus.uga.domain.oauth.service.KakaoOAuthService;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.enums.LoginType;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.ResponseData;
import com.olympus.uga.global.security.jwt.util.JwtProvider;
import com.olympus.uga.domain.mission.service.MissionAssignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.olympus.uga.domain.oauth.presentation.dto.response.GoogleUserInfoDto.registerGoogleUser;
import static com.olympus.uga.domain.oauth.presentation.dto.response.KakaoUserInfoDto.registerKakaoUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthUseCase {
    private final UserJpaRepo userJpaRepo;
    private final KakaoOAuthService kakaoOAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final AppleOAuthService appleOAuthService;
    private final JwtProvider jwtProvider;
    private final MissionAssignService missionAssignService;

    @Transactional
    public ResponseData<SignInRes> loginWithKakaoToken(String accessToken) {
        KakaoUserInfoDto userInfo = kakaoOAuthService.getUserInfo(accessToken);

        User user = userJpaRepo.findByOauthIdAndLoginType(userInfo.id(), LoginType.KAKAO)
                .orElseGet(() -> {
                    User newUser = registerKakaoUser(userInfo);
                    User saved = userJpaRepo.save(newUser);
                    missionAssignService.assignMissionsToNewUser(saved); // 미션부여
                    return saved;
                });

        return ResponseData.ok("카카오 로그인에 성공하였습니다.", jwtProvider.createToken(user.getId()));
    }

    @Transactional
    public ResponseData<SignInRes> loginWithGoogleToken(String accessToken) {
        GoogleUserInfoDto userInfo = googleOAuthService.getUserInfo(accessToken);

        User user = userJpaRepo.findByOauthIdAndLoginType(userInfo.id(), LoginType.GOOGLE)
                .orElseGet(() -> {
                    User newUser = registerGoogleUser(userInfo);
                    User saved = userJpaRepo.save(newUser);
                    missionAssignService.assignMissionsToNewUser(saved);
                    return saved;
                });

        return ResponseData.ok("구글 로그인에 성공하였습니다.", jwtProvider.createToken(user.getId()));
    }

    @Transactional
    public ResponseData<SignInRes> appleLogin(String code) {
        // 1. 애플에서 유저 정보 가져오기
        User appleUser = appleOAuthService.loginWithApple(code);

        // 2. 기존 회원 확인 및 처리
        User user = userJpaRepo.findByOauthIdAndLoginType(appleUser.getOauthId(), LoginType.APPLE)
                .orElseGet(() -> {
                    User saved = userJpaRepo.save(appleUser);
                    missionAssignService.assignMissionsToNewUser(saved);
                    return saved;
                });

        // 기존 회원인 경우 로그인 시간 업데이트
        user.updateLastLoginAt();

        return ResponseData.ok("애플 로그인에 성공하였습니다.", jwtProvider.createToken(user.getId()));
    }
}