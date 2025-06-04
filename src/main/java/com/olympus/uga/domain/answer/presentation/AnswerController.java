package com.olympus.uga.domain.answer.presentation;

import com.olympus.uga.domain.answer.presentation.dto.request.AnswerReq;
import com.olympus.uga.global.common.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {
    private final AnswerService answerService;

    @PostMapping("/{questionId}")
    @Operation(summary = "질문에 답변하기")
    public Response createAnswer(@PathVariable Long questionId, @RequestBody AnswerReq req) {
        return answerService.createAnswer(questionId, req);
    }
}