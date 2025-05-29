package com.olympus.uga.domain.question.presentation.dto.req;

import lombok.Data;

@Data
public class AnswerReq {
    private Long questionId;
    private String answer;
}
