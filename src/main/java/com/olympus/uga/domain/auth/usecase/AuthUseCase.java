package com.olympus.uga.domain.auth.usecase;

import com.olympus.uga.domain.auth.presentation.dto.request.RefreshReq;
import com.olympus.uga.domain.auth.presentation.dto.request.SignInReq;
import com.olympus.uga.domain.auth.presentation.dto.response.RefreshRes;
import com.olympus.uga.domain.auth.presentation.dto.response.SignInRes;
import com.olympus.uga.domain.mission.service.MissionAssignService;
import com.olympus.uga.domain.sms.error.SmsErrorCode;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.auth.error.AuthErrorCode;
import com.olympus.uga.domain.auth.presentation.dto.request.SignUpReq;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.jwt.enums.TokenType;
import com.olympus.uga.global.security.jwt.error.JwtErrorCode;
import com.olympus.uga.global.security.jwt.util.JwtExtractor;
import com.olympus.uga.global.security.jwt.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthUseCase {
    private final UserJpaRepo userJpaRepo;
    private final JwtExtractor jwtExtractor;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final MissionAssignService missionAssignService;

    @Transactional
    public Response signUp(SignUpReq req) {
        if(userJpaRepo.existsByPhoneNum(req.phoneNum())) {
            throw new CustomException(AuthErrorCode.PHONE_NUM_ALREADY);
        }

        String verified = redisTemplate.opsForValue().get(req.phoneNum() + ":verified");
        if (!"true".equals(verified)) {
            throw new CustomException(SmsErrorCode.PHONE_NUM_NOT_VERIFIED);
        }

        User savedUser = userJpaRepo.save(SignUpReq.fromSignUpReq(req, passwordEncoder.encode(req.password())));
        missionAssignService.assignMissionsToNewUser(savedUser); // 미션 부여
        redisTemplate.delete("sms:verified:" + req.phoneNum());

        return Response.created("회원가입에 성공하였습니다.");
    }

    public ResponseData<SignInRes> signIn(SignInReq req) {
        User user = userJpaRepo.findByPhoneNum(req.phoneNum())
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new CustomException(AuthErrorCode.WRONG_PASSWORD);
        }

        return ResponseData.ok("로그인에 성공하였습니다.", jwtProvider.createToken(user.getId()));
    }

    public ResponseData<RefreshRes> refresh(RefreshReq req) {
        if (jwtExtractor.isWrongType(req.refreshToken(), TokenType.REFRESH)) {
            throw new  CustomException(JwtErrorCode.TOKEN_TYPE_ERROR);
        }

        Long userId = jwtExtractor.getUserId(req.refreshToken());

        return ResponseData.created("리프레쉬를 성공하였습니다.", jwtProvider.refreshToken(userId));
    }
}
