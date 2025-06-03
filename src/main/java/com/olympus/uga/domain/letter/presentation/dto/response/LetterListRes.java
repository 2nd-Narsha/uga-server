package com.olympus.uga.domain.letter.presentation.dto.response;

import com.olympus.uga.domain.letter.domain.Letter;

import java.time.LocalDate;

public record LetterListRes(Long letterId, String senderName, LocalDate sentAt) {
    public static LetterListRes from(Letter letter) {
        return new LetterListRes(
                letter.getLetterId(),
                letter.getSender().getUsername(),
                letter.getSentAt()
        );
    }
}