package com.olympus.uga.domain.question.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.olympus.uga.domain.question.domain.Question;

import java.time.LocalDate;

public record QuestionListRes(
        Long questionId,
        String question,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate createdAt,
        boolean hasAnswered) {
    public static QuestionListRes from(Question question, boolean hasAnswered) {
        return new QuestionListRes(
                question.getQuestionId(),
                question.getQuestion(),
                question.getCreatedAt(),
                hasAnswered
        );
    }
}
