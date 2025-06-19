package com.olympus.uga.domain.uga.presentation;

import com.olympus.uga.domain.uga.presentation.dto.request.UgaCreateReq;
import com.olympus.uga.domain.uga.service.UgaService;
import com.olympus.uga.global.common.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
