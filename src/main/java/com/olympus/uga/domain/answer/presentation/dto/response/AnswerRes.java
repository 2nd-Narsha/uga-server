package com.olympus.uga.domain.answer.presentation.dto.response;

import com.olympus.uga.domain.answer.domain.Answer;

public record AnswerRes(Long answerId, String answer, String writerName) {
    public static AnswerRes from(Answer answer) {
        return new AnswerRes(
                answer.getAnswerId(),
                answer.getAnswer(),
                answer.getWriter().getUsername()
        );
    }
}