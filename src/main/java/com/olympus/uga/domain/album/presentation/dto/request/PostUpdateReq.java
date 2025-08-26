package com.olympus.uga.domain.album.presentation.dto.request;

import java.util.List;

public record PostUpdateReq(String content, List<String> imageUrls) {
}
