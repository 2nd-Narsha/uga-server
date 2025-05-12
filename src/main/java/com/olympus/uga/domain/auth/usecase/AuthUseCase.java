package com.olympus.uga.domain.auth.usecase;

import com.olympus.uga.domain.auth.presentation.dto.request.RefreshReq;
import com.olympus.uga.domain.auth.presentation.dto.response.RefreshRes;
import com.olympus.uga.domain.user.error.UserErrorCode;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthUseCase {
    private final UserJpaRepo userJpaRepo;
    private final JwtExtractor jwtExtractor;
    private final JwtProvider jwtProvider;

//    @Transactional
//    public Response signUp(SignUpReq req) {
//        if(userJpaRepo.existsById(req.phoneNum())){
//            throw new CustomException(UserErrorCode.PHONE_NUM_ALREADY);
//        }
//
//        userJpaRepo.save(req.);
//
//        return Response.created("회원가입에 성공하였습니다.");
//    }

    public ResponseData<RefreshRes> refresh(RefreshReq req) {
        if (jwtExtractor.isWrongType(req.refreshToken(), TokenType.REFRESH)) {
            throw new  CustomException(JwtErrorCode.TOKEN_TYPE_ERROR);
        }

        String phoneNum = jwtExtractor.getPhoneNum(req.refreshToken());

        return ResponseData.created("리프레쉬를 성공하였습니다.", jwtProvider.refreshToken(phoneNum));
    }
}
