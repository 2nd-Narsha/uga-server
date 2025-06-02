package com.olympus.uga.domain.point.service;

import com.olympus.uga.domain.point.domain.enums.ActivityType;
import com.olympus.uga.domain.uga.domain.enums.FoodType;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.domain.user.error.UserErrorCode;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserJpaRepo userJpaRepo;

    public int getPoint() {
        return userJpaRepo.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND)).getPoint();
    }

    public Response earnPoint(ActivityType activityType) {
        Map<ActivityType, Integer> activityEffect = Map.of(
                ActivityType.SIGN_UP, 50,
                ActivityType.LETTER, 2,
                ActivityType.ATTENDANCE, 1,
                ActivityType.SEVENTH_ATTENDANCE, 3,
                ActivityType.ANSWER, 3,
                ActivityType.BIRTHDAY, 10
        );

        User user = userJpaRepo.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        user.setPoint(user.getPoint() + activityEffect.get(activityType));

        return Response.ok("현재 포인트 : " + user.getPoint());
    }

    public Response usePoint(FoodType food) {
        Map<FoodType, Integer> foodEffect = Map.of(
                FoodType.BANANA_CHIP, 20,
                FoodType.BANANA, 55,
                FoodType.BANANA_KICK, 120
        );

        User user = userJpaRepo.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        user.getFoods().add(food);
        user.setPoint(user.getPoint() - foodEffect.get(food));

        return Response.ok("현재 포인트 : " + user.getPoint());
    }
}
