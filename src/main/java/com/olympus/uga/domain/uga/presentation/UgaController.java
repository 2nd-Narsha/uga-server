package com.olympus.uga.domain.uga.presentation;

import com.olympus.uga.domain.uga.presentation.dto.request.UgaCreateReq;
import com.olympus.uga.domain.uga.presentation.dto.request.UgaFeedReq;
import com.olympus.uga.domain.uga.presentation.dto.response.CurrentUgaRes;
import com.olympus.uga.domain.uga.presentation.dto.response.UgaListRes;
import com.olympus.uga.domain.uga.service.UgaService;
import com.olympus.uga.global.common.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/uga")
public class UgaController {
    private final UgaService ugaService;

    @PostMapping("/create")
    @Operation(summary = "우가 생성")
    public Response createUga(@RequestBody UgaCreateReq req){
        return ugaService.createUga(req);
    }

    @GetMapping("/current")
    @Operation(summary = "현재 우가 조회")
    public CurrentUgaRes getCurrentUga() {
        return ugaService.getCurrentUga();
    }

//    @PostMapping("/feed")
//    @Operation(summary = "우가 먹이 주기")
//    public Response feedUga(@RequestBody UgaFeedReq req) {
//        return ugaService.feedUga(req);
//    }

    @GetMapping("/dictionary")
    @Operation(summary = "우가 사전")
    public List<UgaListRes> getDictionary() {
        return ugaService.getDictionary();
    }
}
