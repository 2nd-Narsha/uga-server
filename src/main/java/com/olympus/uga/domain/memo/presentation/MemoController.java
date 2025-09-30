package com.olympus.uga.domain.memo.presentation;

import com.olympus.uga.domain.memo.presentation.dto.request.LocationUpdateReq;
import com.olympus.uga.domain.memo.presentation.dto.request.MemoUpdateReq;
import com.olympus.uga.domain.memo.presentation.dto.response.MemoInfoRes;
import com.olympus.uga.domain.memo.service.MemoService;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/memo")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @PostMapping("/create")
    public Response create(MemoUpdateReq req) {
        return memoService.updateMemo(req);
    }

    @PostMapping("/location/update")
    public Response updateLocation(LocationUpdateReq req) {
        return memoService.updateLocation(req);
    }

    @GetMapping("/{userId}")
    public MemoInfoRes getOne(@PathVariable Long userId) {
        return memoService.getOne(userId);
    }
}