package com.olympus.uga.domain.letter.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.olympus.uga.domain.letter.domain.Letter;

import java.time.LocalDate;

public record LetterListRes(
        Long letterId,
        String senderName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate sentAt) {
    public static LetterListRes from(Letter letter) {
        return new LetterListRes(
                letter.getLetterId(),
                letter.getSender().getUsername(),
                letter.getSentAt()
        );
    }
}