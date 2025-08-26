package com.olympus.uga.domain.album.presentation.dto.request;

import com.olympus.uga.domain.album.domain.Comment;
import com.olympus.uga.domain.album.domain.Post;
import com.olympus.uga.domain.user.domain.User;

public record CommentReq(String content) {
    public static Comment fromCommentReq(User writer, Post post, CommentReq req) {
        return Comment.builder()
                .post(post)
                .writer(writer)
                .content(req.content())
                .build();
    }
}