package com.olympus.uga.domain.album.presentation.dto.response;

import com.olympus.uga.domain.album.domain.Post;
import java.time.LocalDate;

public record PostListRes(Long postId,
                          String profileImage,
                          String writerName,
                          String content,
                          String imageUrl,
                          LocalDate createdAt) {
    public static PostListRes from(Post post) {
        return new PostListRes(
                post.getPostId(),
                post.getWriter().getProfileImage(),
                post.getWriter().getUsername(),
                post.getContent(),
                post.getImageUrl(),
                post.getCreatedAt()
        );
    }
}
