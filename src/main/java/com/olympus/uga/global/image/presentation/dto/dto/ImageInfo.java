package com.olympus.uga.global.image.presentation.dto.dto;

import lombok.Data;

@Data
public class ImageInfo {

    private String imageUrl;
    private String imageName;

    public ImageInfo(String imageUrl, String imageName) {
        this.imageUrl = imageUrl;
        this.imageName = imageName;
    }
}
