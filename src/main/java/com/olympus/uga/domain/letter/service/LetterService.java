package com.olympus.uga.domain.letter.service;

import com.olympus.uga.domain.letter.domain.Letter;
import com.olympus.uga.domain.letter.domain.repo.LetterJpaRepo;
import com.olympus.uga.domain.letter.error.LetterErrorCode;
import com.olympus.uga.domain.letter.presentation.dto.request.LetterReq;
import com.olympus.uga.domain.letter.presentation.dto.response.LetterListRes;
import com.olympus.uga.domain.letter.presentation.dto.response.LetterRes;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.error.UserErrorCode;
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
    private final UserJpaRepo userJpaRepo;

    @Transactional
    public Response writeLetter(LetterReq req) {
        User sender = userSessionHolder.getUser();

        User receiver = userJpaRepo.findById(req.receiverId())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        receiver.updateMailBox(false);
        sender.usePoint(req.point());

        letterJpaRepo.save(LetterReq.fromLetterReq(sender, receiver, req));

        return Response.created(receiver.getUsername() + "에게 편지를 보냈습니다.");
    }

    public List<LetterListRes> getInbox() {
        User user = userSessionHolder.getUser();

        List<Letter> receivedLetters = letterJpaRepo.findByReceiver(user);

        user.updateMailBox(true);

        return receivedLetters.stream()
                .map(LetterListRes::from)
                .toList();
    }

    public LetterRes getLetter(Long letterId) {
        User user = userSessionHolder.getUser();

        Letter letter = letterJpaRepo.findByIdAndReceiver(letterId, user)
                .orElseThrow(() -> new CustomException(LetterErrorCode.LETTER_NOT_FOUND));

        // 편지를 읽지 않은 상태라면 읽음 처리
        if (!letter.getIsRead()) {
            letter.markAsRead();
            letterJpaRepo.save(letter); // 변경사항 저장
            user.earnPoint(letter.getPoint());
        }

        return LetterRes.from(letter);
    }

    public Boolean isCheckedLetterBox() {
        return userSessionHolder.getUser().getIsCheckedMailbox();
    }
}
