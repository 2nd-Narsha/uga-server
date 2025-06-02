package com.olympus.uga.domain.point.presentation;

import com.olympus.uga.domain.point.domain.enums.ActivityType;
import com.olympus.uga.domain.point.service.PointService;
import com.olympus.uga.domain.uga.domain.enums.FoodType;
import com.olympus.uga.global.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {
    private final PointService pointService;

    @GetMapping
    public int getPoint() {
        return pointService.getPoint();
    }

    @PostMapping("/earn")
    public Response earnPoint(@RequestParam ActivityType activityType) {
        return pointService.earnPoint(activityType);
    }

    @PostMapping("/use")
    public Response usePoint(@RequestParam FoodType food) {
        return pointService.usePoint(food);
    }
}
