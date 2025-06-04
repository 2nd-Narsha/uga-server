package com.olympus.uga.domain.question.service;

import com.olympus.uga.domain.answer.domain.Answer;
import com.olympus.uga.domain.answer.domain.repo.AnswerJpaRepo;
import com.olympus.uga.domain.answer.presentation.dto.response.AnswerRes;
import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.question.domain.Question;
import com.olympus.uga.domain.question.domain.repo.QuestionJpaRepo;
import com.olympus.uga.domain.question.presentation.dto.request.QuestionReq;
import com.olympus.uga.domain.question.presentation.dto.response.QuestionListRes;
import com.olympus.uga.domain.question.presentation.dto.response.QuestionRes;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionJpaRepo questionJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final FamilyJpaRepo familyJpaRepo;
    private final AnswerJpaRepo answerJpaRepo;

    @Transactional
    public Response createQuestion(QuestionReq req) {
        User user = userSessionHolder.getUser();
        Family userFamily = familyJpaRepo.findByMemberListContaining(user.getId())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        questionJpaRepo.save(QuestionReq.fromQuestionReq(req, userFamily, user));

        return Response.created("질문이 생성되었습니다.");
    }

    @Transactional(readOnly = true)
    public List<QuestionListRes> getQuestionList() {
        User user = userSessionHolder.getUser();
        Family userFamily = familyJpaRepo.findByMemberListContaining(user.getId())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        List<Question> questions = questionJpaRepo.findByFamilyOrderByCreatedAtDesc(userFamily);

        return questions.stream()
                .map(QuestionListRes::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuestionRes getQuestion(Long questionId) {
        User user = userSessionHolder.getUser();
        Family userFamily = familyJpaRepo.findByMemberListContaining(user.getId())
                .orElseThrow(() -> new CustomException(FamilyErrorCode.NOT_FAMILY_MEMBER));

        Question question = questionJpaRepo.findByQuestionIdAndFamily(questionId, userFamily)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없거나 접근 권한이 없습니다."));

        // 현재 사용자가 답변했는지 확인
        boolean hasAnswered = answerJpaRepo.findByQuestionAndWriter(question, user).isPresent();

        // 답변한 경우에만 다른 사람들의 답변 조회
        List<AnswerRes> answers;
        if (hasAnswered) {
            List<Answer> answerList = answerJpaRepo.findByQuestionOrderByCreatedAtAsc(question);
            answers = answerList.stream()
                    .map(AnswerRes::from)
                    .toList();
        } else {
            answers = Collections.emptyList();
        }

        return QuestionRes.of(question, hasAnswered, answers);
    }
}
