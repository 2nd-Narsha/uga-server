package com.olympus.uga.domain.user.service;

import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.enums.UserCharacter;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.presentation.dto.request.InterestReq;
import com.olympus.uga.domain.user.presentation.dto.request.MbtiReq;
import com.olympus.uga.domain.user.presentation.dto.request.TutorialReq;
import com.olympus.uga.domain.user.presentation.dto.request.UsernameBirthGenderReq;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingService {
    private final UserJpaRepo userJpaRepo;
    private final UserSessionHolder userSessionHolder;

    @Transactional
    public Response setUsernameBirthGender(UsernameBirthGenderReq req) {
        User user = userSessionHolder.getUser();

        user.updateUsernameBirthGender(req.username(), req.birth(), req.gender());
        userJpaRepo.save(user);

        return Response.ok("사용자 이름, 생년월일, 성별을 저장하였습니다.");
    }

    @Transactional
    public Response setInterest(InterestReq req) {
        User user = userSessionHolder.getUser();

        user.updateInterest(req.interests());
        userJpaRepo.save(user);

        return Response.ok("사용자 관심 주제를 저장하였습니다.");
    }

    @Transactional
    public Response setCharacter(UserCharacter req) {
        User user = userSessionHolder.getUser();

        user.updateCharacter(req);
        userJpaRepo.save(user);

        return Response.ok("사용자 캐릭터를 저장하였습니다.");
    }

    @Transactional
    public Response setMbti(MbtiReq req) {
        User user = userSessionHolder.getUser();

        user.updateMbti(req.mbti());
        userJpaRepo.save(user);

        return Response.ok("사용자 mbti를 저장하였습니다.");
    }

    @Transactional
    public Response setTutorial(TutorialReq req) {
        User user = userSessionHolder.getUser();

        user.updateTutorial(req.tutorial());
        userJpaRepo.save(user);

        return Response.ok("사용자 튜토리얼 진행도를 저장하였습니다.");
    }
}
