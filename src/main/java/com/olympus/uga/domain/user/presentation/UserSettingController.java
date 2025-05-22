package com.olympus.uga.domain.user.presentation;

import com.olympus.uga.domain.user.domain.enums.UserCharacter;
import com.olympus.uga.domain.user.presentation.dto.request.InterestReq;
import com.olympus.uga.domain.user.presentation.dto.request.MbtiReq;
import com.olympus.uga.domain.user.presentation.dto.request.UsernameBirthGenderReq;
import com.olympus.uga.domain.user.service.UserSettingService;
import com.olympus.uga.global.common.Response;
import io.swagger.v3.oas.annotations.Operation;
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

    @PostMapping("/1")
    @Operation(summary = "이름, 생년월일, 성별", description = "성별: MALE, FEMALE")
    public Response setUsernameBirthGender(@RequestBody UsernameBirthGenderReq req) {
        return userSettingService.setUsernameBirthGender(req);
    }

    @PostMapping("/2")
    @Operation(summary = "관심 주제")
    public Response setInterest(@RequestBody InterestReq req) {
        return userSettingService.setInterest(req);
    }

    @PostMapping("/3")
    @Operation(summary = "캐릭터",
            description = "SOBORO(소보로/고양이), CHUNBOGI(춘복이/강아지), BERINGKEON(베링컨/돼지), BERRY(베리/토끼), CHARLES(찰스/말), BONGGU(봉구/곰),NICHOLAS(니콜라스/여우), MILK(밀크/양), HODU(호두/다람쥐), NAELLEUM(낼름/뱀), OGONGI(오공이/원숭이), PENGDUGI(펭두기/펭귄), ACHIMI(아침이/닭)")
    public Response setCharacter(@RequestBody UserCharacter req) {
        return userSettingService.setCharacter(req);
    }

    @PostMapping("/4")
    @Operation(summary = "mbti")
    public Response setMbti(@RequestBody MbtiReq req) {
        return userSettingService.setMbti(req);
    }
}
