package com.olympus.uga.domain.album.presentation.dto.response;

import com.olympus.uga.domain.album.domain.Post;
import java.time.LocalDate;
import java.util.List;

public record PostRes(String profileImage,
                      String writerName,
                      String content,
                      List<String> imageUrls,
                      LocalDate createdAt,
                      List<CommentRes> comments) {
    public static PostRes from(Post post, List<CommentRes> comments, List<String> imageUrls) {
        return new PostRes(
                post.getWriter().getCharacter().getImageUrl(),
                post.getWriter().getUsername(),
                post.getContent(),
                imageUrls,
                post.getCreatedAt(),
                comments
        );
    }
}