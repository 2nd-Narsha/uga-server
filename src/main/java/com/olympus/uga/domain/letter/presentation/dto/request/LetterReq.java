package com.olympus.uga.domain.letter.presentation.dto.request;

import com.olympus.uga.domain.letter.domain.enums.PaperType;

public record LetterReq(Long receiverId, PaperType paperType, String content) {
}
