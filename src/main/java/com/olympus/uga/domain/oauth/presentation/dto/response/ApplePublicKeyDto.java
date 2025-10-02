package com.olympus.uga.domain.oauth.presentation.dto.response;

import lombok.Data;

@Data
public class ApplePublicKeyDto {
    private String kty;
    private String kid;
    private String use;
    private String alg;
    private String n;
    private String e;
}