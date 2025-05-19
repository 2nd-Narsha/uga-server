package com.olympus.uga.domain.user.presentation;

import com.olympus.uga.domain.user.presentation.dto.CharacterReq;
import com.olympus.uga.domain.user.presentation.dto.InterestReq;
import com.olympus.uga.domain.user.presentation.dto.MbtiReq;
import com.olympus.uga.domain.user.presentation.dto.UsernameBirthGenderReq;
import com.olympus.uga.domain.user.service.UserSettingService;
import com.olympus.uga.global.common.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/setting")
@RequiredArgsConstructor
public class UserSettingController {
    private final UserSettingService userSettingService;

    @PostMapping("/1")
    @Operation(summary = "이름, 생년월일, 성별", description = "성별: MALE, FEMALE")
    public Response setUsernameBirthGender(@RequestBody UsernameBirthGenderReq req) {
        return userSettingService.setUsernameBirth(req);
    }

    @PostMapping("/2")
    @Operation(summary = "관심 주제")
    public Response setInterest(@RequestBody InterestReq req) {
        return userSettingService.setInterest(req);
    }

    @PostMapping("/3")
    @Operation(summary = "캐릭터")
    public Response setCharacter(@RequestParam CharacterReq req) {
        return userSettingService.setCharacter(req);
    }

    @PostMapping("/4")
    @Operation(summary = "mbti")
    public Response setMbti(@RequestBody MbtiReq req) {
        return userSettingService.setMbti(req);
    }
}
