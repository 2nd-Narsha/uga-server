package com.olympus.uga.domain.answer.presentation.dto.request;

import com.olympus.uga.domain.answer.domain.Answer;
import com.olympus.uga.domain.question.domain.Question;
import com.olympus.uga.domain.user.domain.User;

public record AnswerReq(String answer) {
    public static Answer fromAnswerReq(AnswerReq req, Question question, User writer) {
        return Answer.builder()
                .answer(req.answer)
                .question(question)
                .writer(writer)
                .build();
    }
}
