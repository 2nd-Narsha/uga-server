package com.olympus.uga.domain.letter.presentation;

import com.olympus.uga.domain.letter.presentation.dto.request.LetterReq;
import com.olympus.uga.domain.letter.presentation.dto.response.LetterListRes;
import com.olympus.uga.domain.letter.presentation.dto.response.LetterRes;
import com.olympus.uga.domain.letter.service.LetterService;
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
@RequestMapping("/letter")
public class LetterController {
    private final LetterService letterService;

    @PostMapping("/write")
    public Response writeLetter(@RequestBody LetterReq req) {
        return letterService.writeLetter(req);
    }

    @GetMapping("/inbox")
    @Operation(summary = "편지 보관함")
    public List<LetterListRes> getInbox() {
        return letterService.getInbox();
    }

    @GetMapping("/{letterId}")
    @Operation(summary = "편지 상세 조회")
    public LetterRes getLetter(@PathVariable Long letterId) {
        return letterService.getLetter(letterId);
    }
}
