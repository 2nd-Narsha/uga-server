package com.olympus.uga.domain.memo.presentation.dto.req;

import com.olympus.uga.domain.memo.domain.Memo;
import com.olympus.uga.domain.user.domain.User;

import java.time.LocalDateTime;

public record MemoCreateReq() {
    public static Memo fromMemoCreateReq(User writer) {
        return Memo.builder()
                .content("메모가 아직 없습니다.")
                .updatedAt(LocalDateTime.now())
                .writer(writer)
                .location("위치가 아직 없습니다.")
                .build();
    }
}