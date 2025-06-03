package com.olympus.uga.domain.letter.service;

import com.olympus.uga.domain.letter.domain.Letter;
import com.olympus.uga.domain.letter.domain.repo.LetterJpaRepo;
import com.olympus.uga.domain.letter.error.LetterErrorCode;
import com.olympus.uga.domain.letter.presentation.dto.request.LetterReq;
import com.olympus.uga.domain.letter.presentation.dto.response.LetterListRes;
import com.olympus.uga.domain.letter.presentation.dto.response.LetterRes;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LetterService {
    private final LetterJpaRepo letterJpaRepo;
    private final UserSessionHolder userSessionHolder;

    @Transactional
    public Response writeLetter(LetterReq req) {
        User user = userSessionHolder.getUser();

        letterJpaRepo.save(LetterReq.fromLetterReq(user, req));

        return Response.created(req.receiverId() + "에게 편지를 보냈습니다.");
    }

    public List<LetterListRes> getInbox() {
        User currentUser = userSessionHolder.getUser();

        List<Letter> receivedLetters = letterJpaRepo.findByReceiver(currentUser);

        return receivedLetters.stream()
                .map(LetterListRes::from)
                .toList();
    }

    public LetterRes getLetter(Long letterId) {
        User currentUser = userSessionHolder.getUser();

        Letter letter = letterJpaRepo.findByIdAndReceiver(letterId, currentUser)
                .orElseThrow(() -> new CustomException(LetterErrorCode.LETTER_NOT_FOUND));

        return LetterRes.from(letter);
    }
}
