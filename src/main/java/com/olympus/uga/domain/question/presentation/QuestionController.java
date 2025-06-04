package com.olympus.uga.domain.question.presentation;

import com.olympus.uga.domain.question.presentation.dto.request.QuestionReq;
import com.olympus.uga.domain.question.presentation.dto.response.QuestionListRes;
import com.olympus.uga.domain.question.presentation.dto.response.QuestionRes;
import com.olympus.uga.domain.question.service.QuestionService;
import com.olympus.uga.global.common.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping("/create")
    public Response createQuestion(@RequestBody QuestionReq req) {
        return questionService.createQuestion(req);
    }

    @GetMapping("/list")
    @Operation(summary = "가족 질문 목록 조회")
    public List<QuestionListRes> getQuestionList() {
        return questionService.getQuestionList();
    }

    @GetMapping("/{questionId}")
    @Operation(summary = "질문 상세 조회")
    public QuestionRes getQuestion(@PathVariable Long questionId) {
        return questionService.getQuestion(questionId);
    }
}
