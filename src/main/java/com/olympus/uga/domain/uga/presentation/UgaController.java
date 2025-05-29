package com.olympus.uga.domain.uga.presentation;

import com.olympus.uga.domain.family.service.FamilyService;
import com.olympus.uga.domain.uga.presentation.dto.req.UgaCreateReq;
import com.olympus.uga.domain.uga.presentation.dto.req.UgaFeedReq;
import com.olympus.uga.domain.uga.presentation.dto.res.UgaInfoRes;
import com.olympus.uga.domain.uga.presentation.dto.res.UgaListRes;
import com.olympus.uga.domain.uga.service.UgaService;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/uga")
public class UgaController {
    private final UgaService ugaService;

    @PostMapping("/create")
    public Response createFamily(@RequestBody UgaCreateReq req) {
        return ugaService.createUga(req);
    }

    @PostMapping("/feed")
    public Response feedUga(@RequestBody UgaFeedReq req) {
        return ugaService.ugaFeed(req);
    }

    @GetMapping("/get")
    public UgaInfoRes getUga(@RequestParam Long ugaId) {
        return ugaService.getUga(ugaId);
    }

    @GetMapping("/ugalist")
    public List<UgaListRes> getUgaList() {
        return ugaService.getUgaList();
    }
}
