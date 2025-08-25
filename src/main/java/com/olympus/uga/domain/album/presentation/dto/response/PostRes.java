package com.olympus.uga.domain.album.presentation.dto.response;

import com.olympus.uga.domain.album.domain.Post;
import java.time.LocalDate;

public record PostRes(String profileImage,
                      String writerName,
                      String content,
                      String imageUrl,
                      LocalDate createdAt) {
    public static PostRes from(Post post) {
        return new PostRes(
                post.getWriter().getProfileImage(),
                post.getWriter().getUsername(),
                post.getContent(),
                post.getImageUrl(),
                post.getCreatedAt()
        );
    }
}