package com.olympus.uga.domain.auth.presentation.dto.response;

import lombok.Builder;

@Builder
public record RefreshRes(String accessToken) {
}
