package com.olympus.uga.domain.user.service;

import com.olympus.uga.domain.user.presentation.dto.UserSettingReq;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingService {
    @Transactional
    public Response userSetting(UserSettingReq req) {
        return Response.ok("사용자 설정을 저장하였습니다.");
    }
}
