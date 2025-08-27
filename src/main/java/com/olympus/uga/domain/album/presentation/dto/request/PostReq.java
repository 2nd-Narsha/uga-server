package com.olympus.uga.domain.album.presentation.dto.request;

import com.olympus.uga.domain.album.domain.Post;
import com.olympus.uga.domain.album.domain.PostImage;
import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.user.domain.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record PostReq(String content, List<String> imageUrls) {
    public static Post fromPostReq(User writer, Family family, PostReq req) {
        Post post = Post.builder()
                .writer(writer)
                .family(family)  // Family 추가
                .content(req.content())
                .createdAt(LocalDate.now())
                .build();

        // 이미지 처리
        if (req.imageUrls() != null && !req.imageUrls().isEmpty()) {
            List<PostImage> images = new ArrayList<>();
            for (int i = 0; i < req.imageUrls().size(); i++) {
                PostImage image = PostImage.builder()
                        .post(post)
                        .imageUrl(req.imageUrls().get(i))
                        .imageOrder(i + 1)
                        .build();
                images.add(image);
            }
            post.getImages().addAll(images);
        }

        return post;
    }
}