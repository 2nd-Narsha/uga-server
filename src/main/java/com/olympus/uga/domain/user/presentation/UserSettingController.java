package com.olympus.uga.domain.user.presentation;

import com.olympus.uga.domain.user.presentation.dto.UserSettingReq;
import com.olympus.uga.domain.user.service.UserSettingService;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/setting")
@RequiredArgsConstructor
public class UserSettingController {
    private final UserSettingService userSettingService;

    @PostMapping
    public Response userSetting(@RequestBody UserSettingReq req) {
        return userSettingService.userSetting(req);
    }
}
