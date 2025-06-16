package com.olympus.uga.domain.calendar.presentation;

import com.olympus.uga.domain.calendar.presentation.dto.request.DdayReq;
import com.olympus.uga.domain.calendar.presentation.dto.request.DdayUpdateReq;
import com.olympus.uga.domain.calendar.presentation.dto.response.DdayListRes;
import com.olympus.uga.domain.calendar.service.DdayService;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar/dday")
public class DdayController {
    private final DdayService ddayService;

    @GetMapping("/list")
    public List<DdayListRes> getList() {
        return ddayService.getList();
    }

    @PostMapping("/create")
    public Response createDday(@RequestBody DdayReq req) {
        return ddayService.createDday(req);
    }

    @PatchMapping("/update")
    public Response updateDday(@RequestBody DdayUpdateReq req) {
        return ddayService.updateDday(req);
    }

    @DeleteMapping("/delete/{ddayId}")
    public Response deleteDday(@PathVariable Long ddayId) {
        return ddayService.deleteDday(ddayId);
    }
}