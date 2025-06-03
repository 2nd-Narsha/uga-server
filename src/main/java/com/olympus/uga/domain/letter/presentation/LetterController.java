package com.olympus.uga.domain.letter.presentation;

import com.olympus.uga.domain.letter.presentation.dto.request.LetterReq;
import com.olympus.uga.domain.letter.service.LetterService;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/letter")
public class LetterController {
    private final LetterService letterService;

    @PostMapping("/write")
    public Response writeLetter(@RequestBody LetterReq req) {
        return letterService.writeLetter(req);
    }
}
