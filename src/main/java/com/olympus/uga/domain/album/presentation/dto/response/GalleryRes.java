package com.olympus.uga.domain.album.presentation.dto.response;

import com.olympus.uga.domain.album.domain.PostImage;

import java.time.LocalDate;
import java.util.List;

public record GalleryRes(LocalDate date, List<GalleryImageRes> images) {
    public static GalleryRes from(LocalDate date, List<PostImage> images) {
        List<GalleryImageRes> galleryImages = images.stream()
                .map(GalleryImageRes::from)
                .toList();
        return new GalleryRes(date, galleryImages);
    }
}