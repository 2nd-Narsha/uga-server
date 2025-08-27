package com.olympus.uga.domain.album.presentation.dto.response;

import com.olympus.uga.domain.album.domain.Comment;

public record CommentRes(Long commentId,
                         String profileImage,
                         String writerName,
                         String content) {
    public static CommentRes from(Comment comment) {
        return new CommentRes(
                comment.getCommentId(),
                comment.getWriter().getProfileImage(),
                comment.getWriter().getUsername(),
                comment.getContent()
        );
    }
}