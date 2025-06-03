package com.olympus.uga.domain.letter.presentation.dto.response;

import com.olympus.uga.domain.letter.domain.Letter;
import com.olympus.uga.domain.letter.domain.enums.PaperType;

import java.time.LocalDate;

public record LetterRes(String senderName,
                        String receiverName,
                        String content,
                        PaperType paperType,
                        LocalDate sentAt) {
    public static LetterRes from(Letter letter) {
        return new LetterRes(
                letter.getSender().getUsername(), // User 엔티티에 name 필드가 있다고 가정
                letter.getReceiver().getUsername(),
                letter.getContent(),
                letter.getPaperType(),
                letter.getSentAt()
        );
    }
}
