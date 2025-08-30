package com.olympus.uga.domain.memo.presentation.dto.req;

import com.olympus.uga.domain.memo.domain.Memo;
import com.olympus.uga.domain.user.domain.User;

import java.time.LocalDateTime;

public record MemoCreateReq(String content, String location) {
    public static Memo fromMemoCreateReq(User writer, MemoCreateReq req) {
        return Memo.builder()
                .content(req.content)
                .createdAt(LocalDateTime.now())
                .writer(writer)
                .location(req.location)
                .build();
    }
}