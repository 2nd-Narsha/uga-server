package com.olympus.uga.domain.letter.presentation;

import com.olympus.uga.domain.letter.service.LetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/letter")
public class LetterController {
    private final LetterService letterService;
}
