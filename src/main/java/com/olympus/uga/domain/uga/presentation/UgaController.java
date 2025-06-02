package com.olympus.uga.domain.uga.presentation;

import com.olympus.uga.domain.uga.presentation.dto.request.UgaCreateReq;
import com.olympus.uga.domain.uga.presentation.dto.request.UgaFeedReq;
import com.olympus.uga.domain.uga.presentation.dto.response.UgaInfoRes;
import com.olympus.uga.domain.uga.presentation.dto.response.UgaListRes;
import com.olympus.uga.domain.uga.service.UgaService;
import com.olympus.uga.global.common.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/uga")
public class UgaController {
    private final UgaService ugaService;

    @GetMapping
    @Operation(summary = "우가 불러오기")
    public UgaInfoRes getUga(@RequestParam Long ugaId) {
        return ugaService.getUga(ugaId);
    }

    @PostMapping("/create")
    @Operation(summary = "우가 생성")
    public Response createFamily(@RequestBody UgaCreateReq req) {
        return ugaService.createUga(req);
    }

    @PostMapping("/feed")
    @Operation(summary = "우가 먹이주기")
    public Response feedUga(@RequestBody UgaFeedReq req) {
        return ugaService.ugaFeed(req);
    }

    @GetMapping("/ugaList")
    @Operation(summary = "우가 사전")
    public List<UgaListRes> getUgaList() {
        return ugaService.getUgaList();
    }
}
