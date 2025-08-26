package com.olympus.uga.domain.album.presentation.dto.response;

import java.time.LocalDate;
import java.util.List;

public record GalleryRes(LocalDate date, List<String> imageUrls) {
}