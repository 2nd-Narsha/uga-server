package com.olympus.uga.domain.sms.presentation;

import com.olympus.uga.domain.sms.presentation.dto.request.SmsSendingReq;
import com.olympus.uga.domain.sms.service.SmsService;
import com.olympus.uga.global.common.Response;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "인증코드 문자전송", description = "전화번호 입력 시 '-' 없이 입력")
    public Response sendMessage(@RequestBody SmsSendingReq req) {
        return smsService.sendMessage(req);
    }

    @GetMapping("/verify")
    @Operation(summary = "인증코드 확인", description = "전화번호 입력 시 '-' 없이 입력")
    public Response verifyCode(@RequestParam("phoneNum") String phoneNum, @RequestParam("code") String code) {
        return smsService.verifyCode(phoneNum, code);
    }
}
