package com.olympus.uga.domain.auth.usecase;

import com.olympus.uga.domain.auth.error.AuthErrorCode;
import com.olympus.uga.domain.auth.presentation.dto.request.SignInReq;
import com.olympus.uga.domain.auth.presentation.dto.request.SignUpReq;
import com.olympus.uga.domain.auth.presentation.dto.response.SignInRes;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import com.olympus.uga.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthUseCase {
    private final UserJpaRepo userJpaRepo;

    @Transactional
    public Response signUp(SignUpReq req) {
        if(userJpaRepo.existsById(req.phoneNum())){
            throw new CustomException(AuthErrorCode.PHONE_NUM_ALREADY);
        }

        userJpaRepo.save(req.);

        return Response.created("회원가입에 성공하였습니다.");
    }
}
