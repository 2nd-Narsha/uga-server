package com.olympus.uga.domain.attend.presentation;

import com.olympus.uga.domain.attend.presentation.dto.response.AttendStatusRes;
import com.olympus.uga.domain.attend.service.AttendService;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attend")
public class AttendController {
    private final AttendService attendService;

    @GetMapping("/status")
    public AttendStatusRes getAttendStatus() {
        return attendService.getAttendStatus();
    }

    @PostMapping("/check")
    public Response checkAttend() {
        return attendService.checkAttend();
    }
}
