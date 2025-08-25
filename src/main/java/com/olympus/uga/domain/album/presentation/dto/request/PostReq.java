package com.olympus.uga.domain.album.presentation.dto.request;

import com.olympus.uga.domain.album.domain.Post;
import com.olympus.uga.domain.user.domain.User;

import java.time.LocalDate;

public record PostReq(String content, String imageUrl) {
    public static Post fromPostReq(User writer, PostReq req) {
        return Post.builder()
                .writer(writer)
                .content(req.content)
                .imageUrl(req.imageUrl)
                .createdAt(LocalDate.now())
                .build();
    }
}