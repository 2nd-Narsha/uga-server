package com.olympus.uga.domain.sms.presentation;

import com.olympus.uga.domain.sms.presentation.dto.request.SmsSendingReq;
import com.olympus.uga.domain.sms.service.SmsService;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class SmsController {
    private final SmsService smsService;

    @PostMapping("/send")
    public void sendMessage(@RequestBody SmsSendingReq req) {
        Response.of(smsService.sendMessage(req));
    }

    @GetMapping("/verify")
    public void verifyMessage(@RequestParam String phoneNum, @RequestParam String code) {
        Response.of(smsService.verifyMessage(phoneNum, code));
    }
}
