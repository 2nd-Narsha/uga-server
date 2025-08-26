package com.olympus.uga.domain.album.presentation.dto.request;

import com.olympus.uga.domain.album.domain.Post;
import com.olympus.uga.domain.album.domain.PostImage;
import com.olympus.uga.domain.user.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

public record PostReq(String content, List<String> imageUrls) {
    public static Post fromPostReq(User writer, PostReq req) {
        Post post = Post.builder()
                .writer(writer)
                .content(req.content())
                .createdAt(LocalDate.now())
                .build();

        // 이미지 처리
        if (req.imageUrls() != null && !req.imageUrls().isEmpty()) {
            List<PostImage> images = IntStream.range(0, req.imageUrls().size())
                    .mapToObj(i -> PostImage.builder()
                            .post(post)
                            .imageUrl(req.imageUrls().get(i))
                            .imageOrder(i + 1)
                            .build())
                    .toList();
            post.getImages().addAll(images);
        }

        return post;
    }
}