package com.olympus.uga.domain.letter.presentation.dto.request;

import com.olympus.uga.domain.letter.domain.Letter;
import com.olympus.uga.domain.letter.domain.enums.PaperType;
import com.olympus.uga.domain.user.domain.User;

public record LetterReq(User receiverId, PaperType paperType, String content) {
    public static Letter fromLetterReq(User sender, LetterReq req) {
        return Letter.builder()
                .sender(sender)
                .receiver(req.receiverId)
                .paperType(req.paperType)
                .content(req.content)
                .build();
    }
}
