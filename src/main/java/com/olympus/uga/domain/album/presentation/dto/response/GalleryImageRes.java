package com.olympus.uga.domain.album.presentation.dto.response;

import com.olympus.uga.domain.album.domain.PostImage;

public record GalleryImageRes(Long postId, String imageUrl, String writerName, String profileImage) {
    public static GalleryImageRes from(PostImage postImage) {
        return new GalleryImageRes(
                postImage.getPost().getPostId(),
                postImage.getImageUrl(),
                postImage.getPost().getWriter().getUsername(),
                postImage.getPost().getWriter().getProfileImage()
        );
    }
}
