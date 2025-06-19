package com.olympus.uga.domain.letter.presentation.dto.request;

import com.olympus.uga.domain.letter.domain.Letter;
import com.olympus.uga.domain.letter.domain.enums.PaperType;
import com.olympus.uga.domain.user.domain.User;

import java.time.LocalDate;

public record LetterReq(Long receiverId, PaperType paperType, String content) {
    public static Letter fromLetterReq(User sender, User receiver, LetterReq req) {
        return Letter.builder()
                .sender(sender)
                .receiver(receiver)
                .paperType(req.paperType)
                .content(req.content)
                .sentAt(LocalDate.now())
                .isRead(false)
                .build();
    }
}
