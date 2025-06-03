package com.olympus.uga.domain.letter.service;

import com.olympus.uga.domain.letter.domain.repo.LetterJpaRepo;
import com.olympus.uga.domain.letter.presentation.dto.request.LetterReq;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LetterService {
    private final LetterJpaRepo letterJpaRepo;
    private final UserSessionHolder userSessionHolder;

    public Response writeLetter(LetterReq req) {
        User user = userSessionHolder.getUser();

        letterJpaRepo.save(LetterReq.fromLetterReq(user, req));

        return Response.created(req.receiverId() + "에게 편지를 보냈습니다.");
    }
}
