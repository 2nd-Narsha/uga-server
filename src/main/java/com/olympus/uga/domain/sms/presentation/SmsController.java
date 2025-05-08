package com.olympus.uga.domain.sms.presentation;

import com.olympus.uga.domain.sms.presentation.dto.request.SmsSendingReq;
import com.olympus.uga.domain.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send")
    public void sendMessage(@RequestBody SmsSendingReq req){

    }

    //    @PostMapping("/send")
    //    fun sendEmail(@RequestBody request: EmailSendingRequest) =
    //        BaseResponse.of(emailService.sendEmail(request))
    //
    //    @GetMapping("/verify")
    //    fun verifyEmail(@RequestParam email: String, @RequestParam code: String) =
    //        BaseResponse.of(emailService.verifyEmail(email, code))
}
