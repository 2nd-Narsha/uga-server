package com.olympus.uga.domain.album.presentation.dto.request;

import com.olympus.uga.domain.album.domain.Post;
import com.olympus.uga.domain.user.domain.User;

import java.time.LocalDate;
import java.util.List;

public record PostReq(String content, List<String> imageUrls) {
    public static Post fromPostReq(User writer, PostReq req) {
        return Post.builder()
                .writer(writer)
                .content(req.content)
                .createdAt(LocalDate.now())
                .build();
    }
}