package com.olympus.uga.domain.point.service;

import com.olympus.uga.domain.family.domain.repo.FamilyJpaRepo;
import com.olympus.uga.domain.point.domain.enums.ActivityType;
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
    private final FamilyJpaRepo familyJpaRepo;

    private static final Map<ActivityType, Integer> ACTIVITY_POINTS = Map.of(
            ActivityType.SIGN_UP, 50,
            ActivityType.LETTER, 2,
            ActivityType.ANSWER, 3,
            ActivityType.BIRTHDAY, 10
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
}
