package com.olympus.uga.domain.letter.presentation;

import com.olympus.uga.domain.letter.domain.Letter;
import com.olympus.uga.domain.letter.presentation.dto.req.LetterCreateReq;
import com.olympus.uga.domain.letter.service.LetterService;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/letter")
public class LetterController {
    private final LetterService letterService;

    @PostMapping("/create")
    public Response create(@RequestBody LetterCreateReq req) {
        return letterService.createLetter(req);
    }

    @GetMapping("/inbox")
    public List<Letter> getInbox() {
        return letterService.getLetters();
    }
}
