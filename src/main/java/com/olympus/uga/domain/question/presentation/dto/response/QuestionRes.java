package com.olympus.uga.domain.question.presentation.dto.response;

import com.olympus.uga.domain.answer.presentation.dto.response.AnswerRes;
import com.olympus.uga.domain.question.domain.Question;

import java.time.LocalDate;
import java.util.List;

public record QuestionRes(String question, LocalDate createdAt, boolean hasAnswered, List<AnswerRes> answers) {
    public static QuestionRes of(Question question, boolean hasAnswered, List<AnswerRes> answers) {
        return new QuestionRes(
                question.getQuestion(),
                question.getCreatedAt(),
                hasAnswered,
                answers
        );
    }
}

