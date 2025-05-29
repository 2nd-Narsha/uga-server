package com.olympus.uga.domain.question.service;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.family.domain.repo.FamilyRepo;
import com.olympus.uga.domain.family.error.FamilyErrorCode;
import com.olympus.uga.domain.question.domain.Answer;
import com.olympus.uga.domain.question.domain.Question;
import com.olympus.uga.domain.question.domain.repo.AnswerRepo;
import com.olympus.uga.domain.question.domain.repo.QuestionRepo;
import com.olympus.uga.domain.question.error.QuestionErrorCode;
import com.olympus.uga.domain.question.presentation.dto.req.AnswerReq;
import com.olympus.uga.domain.question.presentation.dto.req.QuestionCreateReq;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.error.UserErrorCode;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepo questionRepo;
    private final AnswerRepo answerRepo;
    private final FamilyRepo familyRepo;
    private final UserJpaRepo userJpaRepo;

    @Transactional
    public Response create(QuestionCreateReq req) {

        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        Family currentFamily = familyRepo.findAll()
                .stream()
                .filter(f -> f.getMemberList().contains(currentUserId))
                .findFirst()
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        User currentUser = userJpaRepo.findById(currentUserId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        questionRepo.save(new Question(req, currentFamily, currentUser));


        return Response.created("질문이 생성되었습니다.");
    }

    public Response answer(AnswerReq req) {

        User currentUser = userJpaRepo.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        Question question = questionRepo.findById(req.getQuestionId())
                .orElseThrow(() -> new CustomException(QuestionErrorCode.QUESTION_NOT_FOUND));

        Optional<Answer> existingAnswerOpt = answerRepo.findByQuestionAndWriter(question, currentUser);

        if (existingAnswerOpt.isPresent()) {
            Answer existingAnswer = existingAnswerOpt.get();
            existingAnswer.setAnswer(req.getAnswer());
            answerRepo.save(existingAnswer);
        } else {
            answerRepo.save(new Answer(req.getAnswer(), currentUser, question));
        }

        return Response.ok("답변이 저장되었습니다.");
    }

    public List<Question> getQuestions() {

        Family family = familyRepo.findAll()
                .stream()
                .filter(f -> f.getMemberList().contains(SecurityContextHolder.getContext().getAuthentication().getName()))
                .findFirst()
                .orElseThrow(() -> new CustomException(FamilyErrorCode.FAMILY_NOT_FOUND));

        return questionRepo.findByFamily(family);
    }

    public List<Answer> getAnswers(Long questionId) {

        return answerRepo.findByQuestion(questionRepo.findById(questionId).orElseThrow(() -> new CustomException(QuestionErrorCode.QUESTION_NOT_FOUND)));

    }
}