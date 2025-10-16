package com.olympus.uga.domain.album.presentation.dto.response;

import com.olympus.uga.domain.album.domain.Post;
import java.time.LocalDate;
import java.util.List;

public record PostListRes(Long postId,
                          String profileImage,
                          String writerName,
                          String content,
                          List<String> imageUrls,
                          LocalDate createdAt,
                          Long commentCount) {
    public static PostListRes from(Post post, Long commentCount, List<String> imageUrls) {
        return new PostListRes(
                post.getPostId(),
                post.getWriter().getCharacter().getImageUrl(),
                post.getWriter().getUsername(),
                post.getContent(),
                imageUrls,
                post.getCreatedAt(),
                commentCount
        );
    }
}