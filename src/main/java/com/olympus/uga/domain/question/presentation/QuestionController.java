package com.olympus.uga.domain.question.presentation;

import com.olympus.uga.domain.question.domain.Answer;
import com.olympus.uga.domain.question.domain.Question;
import com.olympus.uga.domain.question.presentation.dto.req.AnswerReq;
import com.olympus.uga.domain.question.presentation.dto.req.QuestionCreateReq;
import com.olympus.uga.domain.question.service.QuestionService;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/create")
    public Response createQuestion(@RequestBody QuestionCreateReq req) {
        return questionService.create(req);
    }

    @PostMapping("/answer")
    public Response answerQuestion(@RequestBody AnswerReq req) {
        return questionService.answer(req);
    }

    @GetMapping("/questionlist")
    public List<Question> getQuestions() {
        return questionService.getQuestions();
    }

    @GetMapping("/answerlist")
    public List<Answer> getAnswers(@RequestParam Long questionId) {
        return questionService.getAnswers(questionId);
    }
}
