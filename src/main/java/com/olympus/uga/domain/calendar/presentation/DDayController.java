package com.olympus.uga.domain.calendar.presentation;

import com.olympus.uga.domain.calendar.presentation.dto.request.DDayReq;
import com.olympus.uga.domain.calendar.presentation.dto.request.DDayUpdateReq;
import com.olympus.uga.domain.calendar.presentation.dto.response.DDayListRes;
import com.olympus.uga.domain.calendar.service.DDayService;
import com.olympus.uga.global.common.Response;
import io.swagger.v3.oas.annotations.Operation;
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
public class DDayController {
    private final DDayService ddayService;

    @GetMapping("/list")
    @Operation(summary = "디데이 목록 조회")
    public List<DDayListRes> getList() {
        return ddayService.getList();
    }

    @PostMapping("/create")
    @Operation(summary = "디데이 생성", description = "가족 구성원만 접근 가능합니다.")
    public Response createDday(@RequestBody DDayReq req) {
        return ddayService.createDday(req);
    }

    @PatchMapping("/update")
    @Operation(summary = "디데이 수정", description = "가족 구성원만 접근 가능합니다.")
    public Response updateDday(@RequestBody DDayUpdateReq req) {
        return ddayService.updateDday(req);
    }

    @DeleteMapping("/delete/{ddayId}")
    @Operation(summary = "디데이 삭제", description = "가족 구성원만 접근 가능합니다.")
    public Response deleteDday(@PathVariable Long ddayId) {
        return ddayService.deleteDday(ddayId);
    }
}