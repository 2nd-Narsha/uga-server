package com.olympus.uga.domain.question.presentation.dto.response;

import com.olympus.uga.domain.question.domain.Question;

import java.time.LocalDate;

public record QuestionListRes(Long questionId, String question, LocalDate createdAt) {
    public static QuestionListRes from(Question question) {
        return new QuestionListRes(
                question.getQuestionId(),
                question.getQuestion(),
                question.getCreatedAt()
        );
    }
}
