package com.olympus.uga.domain.question.presentation.dto.request;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.question.domain.Question;
import com.olympus.uga.domain.user.domain.User;

import java.time.LocalDate;

public record QuestionReq(String question) {
    public static Question fromQuestionReq(QuestionReq req, Family family, User writer) {
        return Question.builder()
                .question(req.question)
                .createdAt(LocalDate.now())
                .family(family)
                .writer(writer)
                .build();
    }
}
