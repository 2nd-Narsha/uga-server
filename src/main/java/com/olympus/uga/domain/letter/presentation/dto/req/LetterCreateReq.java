package com.olympus.uga.domain.letter.presentation.dto.req;

import com.olympus.uga.domain.letter.domain.enums.PaperType;
import lombok.Data;

@Data
public class LetterCreateReq {
    private String content;
    private String receiverPhoneNum;
    private PaperType paperType;
}
