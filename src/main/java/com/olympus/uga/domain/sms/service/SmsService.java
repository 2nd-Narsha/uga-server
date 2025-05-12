package com.olympus.uga.domain.sms.service;

import com.olympus.uga.domain.sms.error.SmsErrorCode;
import com.olympus.uga.domain.sms.presentation.dto.request.SmsSendingReq;
import com.olympus.uga.domain.sms.util.SmsUtil;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SmsService {
    private final SmsUtil smsUtil;
    private final StringRedisTemplate redisTemplate;
    private final long VERIFICATION_CODE_TTL = 300; // 초 (5분)

    public Response sendMessage(SmsSendingReq req) {
        String phoneNum = req.phoneNum();
        String code = generateCode();

        smsUtil.sendOne(phoneNum, code);

        redisTemplate.opsForValue().set(phoneNum, code, VERIFICATION_CODE_TTL, TimeUnit.SECONDS);

        return Response.ok("인증코드가 전송되었습니다.");
    }

    public Response verifyCode(String phoneNum, String code) {
        String savedCode = redisTemplate.opsForValue().get(phoneNum);

        if (savedCode == null) {
            throw new CustomException(SmsErrorCode.CODE_NOT_FOUND); // 존재 X (만료 or 안 보냄)
        }

        if (!savedCode.equals(code)) {
            throw new CustomException(SmsErrorCode.CODE_MISMATCH);
        }

        redisTemplate.delete(phoneNum); // 검증 성공 → Redis에서 삭제

        return Response.ok("인증이 완료되었습니다.");
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}