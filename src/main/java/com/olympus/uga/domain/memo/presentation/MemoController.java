package com.olympus.uga.domain.memo.presentation;

import com.olympus.uga.domain.memo.presentation.dto.req.LocationUpdateReq;
import com.olympus.uga.domain.memo.presentation.dto.req.MemoCreateReq;
import com.olympus.uga.domain.memo.presentation.dto.res.MemoInfoRes;
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
    public Response create(MemoCreateReq req) {
        return memoService.save(req);
    }

    @PostMapping("/location/update")
    public Response updateLocation(LocationUpdateReq req) {
        return memoService.updateLocation(req);
    }

    @GetMapping("/{userId}")
    public MemoInfoRes getOne(@PathVariable Long userId) {
        return memoService.getOne(userId);
    }

    @GetMapping("/ischecked")
    public List<Long> getChecked() {
        return memoService.checkedMember();
    }
}