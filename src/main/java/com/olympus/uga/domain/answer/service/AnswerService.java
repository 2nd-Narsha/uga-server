package com.olympus.uga.domain.answer.service;

import com.olympus.uga.domain.answer.domain.Answer;
import com.olympus.uga.domain.answer.domain.repo.AnswerJpaRepo;
import com.olympus.uga.domain.answer.error.AnswerErrorCode;
import com.olympus.uga.domain.answer.presentation.dto.request.AnswerReq;
import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.question.domain.Question;
import com.olympus.uga.domain.question.domain.repo.QuestionJpaRepo;
import com.olympus.uga.domain.question.error.QuestionErrorCode;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerJpaRepo answerJpaRepo;
    private final QuestionJpaRepo questionJpaRepo;
    private final FamilyJpaRepo familyJpaRepo;
    private final UserSessionHolder userSessionHolder;

    @Transactional
    public Response createAnswer(Long questionId, AnswerReq req) {
        User user = userSessionHolder.getUser();
        Family userFamily = familyJpaRepo.findByMemberListContaining(user.getId())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        Question question = questionJpaRepo.findByQuestionIdAndFamily(questionId, userFamily)
                .orElseThrow(() -> new CustomException(QuestionErrorCode.QUESTION_NOT_FOUND));

        // 이미 답변했는지 확인
        boolean hasAnswered = answerJpaRepo.findByQuestionAndWriter(question, user).isPresent();
        if (hasAnswered) {
            throw new CustomException(AnswerErrorCode.ALREADY_ANSWER);
        }

        answerJpaRepo.save(AnswerReq.fromAnswerReq(req, question, user));

        return Response.created("답변이 등록되었습니다.");
    }
}