package com.olympus.uga.domain.letter.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.olympus.uga.domain.letter.domain.Letter;

import java.time.LocalDate;

public record LetterListRes(
        Long letterId,
        String profileImage,
        String senderName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate sentAt,
        Boolean isRead) {
    public static LetterListRes from(Letter letter) {
        return new LetterListRes(
                letter.getLetterId(),
                letter.getSender().getProfileImage(),
                letter.getSender().getUsername(),
                letter.getSentAt(),
                letter.getIsRead()
        );
    }
}