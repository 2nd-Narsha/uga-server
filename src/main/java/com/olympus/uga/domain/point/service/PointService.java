package com.olympus.uga.domain.point.service;

import com.olympus.uga.domain.point.domain.enums.ActivityType;
import com.olympus.uga.domain.uga.domain.enums.FoodType;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserJpaRepo userJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private static final Map<ActivityType, Integer> ACTIVITY_POINTS = Map.of(
            ActivityType.SIGN_UP, 50,
            ActivityType.LETTER, 2,
            ActivityType.ATTENDANCE, 1,
            ActivityType.SEVENTH_ATTENDANCE, 3,
            ActivityType.ANSWER, 3,
            ActivityType.BIRTHDAY, 10
    );
    private static final Map<FoodType, Integer> FOOD_PRICES = Map.of(
            FoodType.BANANA_CHIP, 20,
            FoodType.BANANA, 55,
            FoodType.BANANA_KICK, 120
    );

    public int getPoint() {
        User user = userSessionHolder.getUser();

        return user.getPoint();
    }

    @Transactional
    public Response earnPoint(ActivityType activityType) {
        User user = userSessionHolder.getUser();

        int earnedPoints = ACTIVITY_POINTS.getOrDefault(activityType, 0);
        user.earnPoint(earnedPoints);

        userJpaRepo.save(user);

        return Response.ok("현재 포인트: " + user.getPoint());
    }

    @Transactional
    public Response usePoint(FoodType food) {
        User user = userSessionHolder.getUser();

        int foodPrice = FOOD_PRICES.getOrDefault(food, 0);
        user.usePoint(foodPrice);

        userJpaRepo.save(user);

        return Response.ok("현재 포인트: " + user.getPoint());
    }
}
