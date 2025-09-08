package com.olympus.uga.domain.uga.presentation;

import com.olympus.uga.domain.uga.presentation.dto.request.UgaChangeCharacterReq;
import com.olympus.uga.domain.uga.presentation.dto.request.UgaChangeColorReq;
import com.olympus.uga.domain.uga.presentation.dto.request.UgaPurchaseCharacterReq;
import com.olympus.uga.domain.uga.presentation.dto.request.UgaPurchaseColorReq;
import com.olympus.uga.domain.uga.presentation.dto.response.UgaDecoRes;
import com.olympus.uga.domain.uga.service.UgaDecoService;
import com.olympus.uga.global.common.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/uga/deco")
public class UgaDecoController {
    private final UgaDecoService ugaDecoService;

    @PostMapping("/purchase/color")
    @Operation(summary = "우가 색상 구매", description = "각 20포인트")
    public Response purchaseColor(@RequestBody UgaPurchaseColorReq req) {
        return ugaDecoService.purchaseColor(req);
    }

    @PostMapping("/purchase/character")
    @Operation(summary = "우가 캐릭터 구매", description = "각 200포인트")
    public Response purchaseCharacter(@RequestBody UgaPurchaseCharacterReq req) {
        return ugaDecoService.purchaseCharacter(req);
    }

    @PostMapping("/change/color")
    @Operation(summary = "우가 색상 변경", description = "보유한 색상으로만 변경 가능")
    public Response changeColor(@RequestBody UgaChangeColorReq req) {
        return ugaDecoService.changeColor(req);
    }

    @PostMapping("/change/character")
    @Operation(summary = "우가 캐릭터 변경", description = "보유한 캐릭터로만 변경 가능")
    public Response changeCharacter(@RequestBody UgaChangeCharacterReq req) {
        return ugaDecoService.changeCharacter(req);
    }

    @GetMapping
    @Operation(summary = "우가 꾸미기 아이템 조회", description = "보유한 아이템과 구매 가능한 아이템 목록")
    public UgaDecoRes getDecoItems() {
        return ugaDecoService.getDecoItems();
    }
}
