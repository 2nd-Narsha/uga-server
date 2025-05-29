package com.olympus.uga.domain.oauth.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUserResponseDto {
    private String sub;
    private String name;
    private String email;
    private String picture;
}
