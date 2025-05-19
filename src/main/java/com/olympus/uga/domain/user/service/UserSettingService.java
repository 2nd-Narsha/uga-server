package com.olympus.uga.domain.user.service;

import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.presentation.dto.CharacterReq;
import com.olympus.uga.domain.user.presentation.dto.InterestReq;
import com.olympus.uga.domain.user.presentation.dto.MbtiReq;
import com.olympus.uga.domain.user.presentation.dto.UsernameBirthGenderReq;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingService {
    private final UserJpaRepo userJpaRepo;

    @Transactional
    public Response setUsernameBirth(UsernameBirthGenderReq req) {
        return Response.ok("사용자 이름, 생년월일, 성별을 저장하였습니다.");
    }

    @Transactional
    public Response setInterest(InterestReq req) {
        return Response.ok("사용자 관심 주제를 저장하였습니다.");
    }

    @Transactional
    public Response setCharacter(CharacterReq req) {
        return Response.ok("사용자 캐릭터를 저장하였습니다.");
    }

    @Transactional
    public Response setMbti(MbtiReq req) {
        return Response.ok("사용자 mbti를 저장하였습니다.");
    }
}
