package com.olympus.uga.domain.oauth.google.presentation;

import com.olympus.uga.domain.oauth.google.presentation.dto.request.LoginReq;
import com.olympus.uga.domain.oauth.google.presentation.dto.response.LoginRes;
import com.olympus.uga.domain.oauth.google.service.GoogleService;
import com.olympus.uga.global.common.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth/google")
@RequiredArgsConstructor
public class GoogleController {
    private final GoogleService googleService;

    @PostMapping("/login")
    public ResponseData<LoginRes> login(@Valid @RequestBody LoginReq req) {
        return googleService.login(req);
    }
}
