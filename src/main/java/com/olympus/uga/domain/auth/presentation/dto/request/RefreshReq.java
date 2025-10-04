package com.olympus.uga.domain.auth.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshReq(@JsonProperty("token") String refreshToken){
}
