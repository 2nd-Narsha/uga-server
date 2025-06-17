package com.olympus.uga.domain.calendar.presentation;

import com.olympus.uga.domain.calendar.presentation.dto.request.ScheduleReq;
import com.olympus.uga.domain.calendar.presentation.dto.request.ScheduleUpdateReq;
import com.olympus.uga.domain.calendar.presentation.dto.response.DdayListRes;
import com.olympus.uga.domain.calendar.presentation.dto.response.ScheduleListRes;
import com.olympus.uga.domain.calendar.service.ScheduleService;
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
@RequestMapping("/calendar/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/list")
    @Operation(summary = "일정 목록 조회")
    public List<ScheduleListRes> getList() {
        return scheduleService.getList();
    }

    @PostMapping("/create")
    @Operation(summary = "일정 생성", description = "가족 구성원만 접근 가능합니다. / 시간은 09:00 이런 형식으로 적어주세요. / 참여자는 유저 아이디를 리스트 형식으로")
    public Response createSchedule(@RequestBody ScheduleReq req) {
        return scheduleService.createSchedule(req);
    }

    @PatchMapping("/update")
    @Operation(summary = "일정 수정", description = "가족 구성원만 접근 가능합니다. / 시간은 09:00 이런 형식으로 적어주세요. / 참여자는 유저 아이디를 리스트 형식으로")
    public Response updateSchedule(@RequestBody ScheduleUpdateReq req) {
        return scheduleService.updateSchedule(req);
    }

    @DeleteMapping("/delete/{scheduleId}")
    @Operation(summary = "일정 삭제", description = "가족 구성원만 접근 가능합니다.")
    public Response deleteSchedule(@PathVariable Long scheduleId) {
        return scheduleService.deleteSchedule(scheduleId);
    }
}
