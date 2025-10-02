package com.olympus.uga.domain.oauth.presentation.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ApplePublicKeysDto {
    private List<ApplePublicKeyDto> keys;
}