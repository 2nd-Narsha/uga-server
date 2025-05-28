package com.olympus.uga.domain.letter.service;

import com.olympus.uga.domain.letter.domain.Letter;
import com.olympus.uga.domain.letter.domain.repo.LetterRepo;
import com.olympus.uga.domain.letter.error.LetterErrorCode;
import com.olympus.uga.domain.letter.presentation.dto.req.LetterCreateReq;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.error.UserErrorCode;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LetterService {

    private final LetterRepo letterRepo;
    private final UserJpaRepo userJpaRepo;

    public Response createLetter(LetterCreateReq req) {

        Letter letter = new Letter(
                req.getContent(),
                req.getPaperType(),
                userJpaRepo.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND)));

        letterRepo.save(letter);

        userJpaRepo.findById(req.getReceiverPhoneNum())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND)).getLettersId().add(letter.getLetterId());

        return Response.created("편지가 생성되었습니다.");
    }

    public List<Letter> getLetters() {

        return userJpaRepo.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND))
                .getLettersId()
                .stream()
                .map(id -> letterRepo.findById(id)
                        .orElseThrow(() -> new CustomException(LetterErrorCode.LETTER_NOT_FOUND)))
                .toList();
    }
}
