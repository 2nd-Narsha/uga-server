package com.olympus.uga.domain.mission.service;

import com.olympus.uga.domain.mission.domain.UserMission;
import com.olympus.uga.domain.mission.domain.enums.StatusType;
import com.olympus.uga.domain.mission.domain.repo.UserMissionJpaRepo;
import com.olympus.uga.domain.mission.error.MissionErrorCode;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import com.olympus.uga.global.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionRewardService {
    private final UserMissionJpaRepo userMissionJpaRepo;
    private final UserJpaRepo userJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final WebSocketService webSocketService;

    @Transactional
    public Response claimReward(Long missionId) {
        User user = userSessionHolder.getUser();
        UserMission userMission = findUserMissionById(missionId);

        validateMissionOwnership(userMission, user);
        validateRewardAvailability(userMission);

        giveReward(user, userMission, "미션 완료 보상");

        return Response.ok("보상을 성공적으로 받았습니다.");
    }

    @Transactional
    public Response claimDailyBonus() {
        User user = userSessionHolder.getUser();
        UserMission dailyBonus = findDailyBonusMission(user);

        validateRewardAvailability(dailyBonus);

        giveReward(user, dailyBonus, "일일 미션 완료 보너스");

        return Response.ok("일일 미션 완료 보너스를 받았습니다!");
    }

    private UserMission findUserMissionById(Long missionId) {
        return userMissionJpaRepo.findById(missionId)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MISSION_NOT_FOUND));
    }

    private UserMission findDailyBonusMission(User user) {
        return userMissionJpaRepo.findDailyBonusByUser(user)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MISSION_NOT_FOUND));
    }

    private void validateMissionOwnership(UserMission userMission, User user) {
        if (!userMission.getUser().getId().equals(user.getId())) {
            throw new CustomException(MissionErrorCode.MISSION_ACCESS_DENIED);
        }
    }

    private void validateRewardAvailability(UserMission userMission) {
        if (userMission.getStatus() != StatusType.WAITING_REWARD) {
            throw new CustomException(MissionErrorCode.REWARD_NOT_AVAILABLE);
        }
    }

    private void giveReward(User user, UserMission userMission, String reason) {
        int rewardPoints = userMission.getMissionList().getRewardPoints();

        user.earnPoint(rewardPoints);
        userMission.claimReward();
        user.updateLastActivityAt();
        userJpaRepo.save(user);

        webSocketService.notifyPointUpdate(user.getId(), user.getPoint(), reason);

        log.info("보상 지급: 사용자 {}, 미션 '{}', 포인트 {}",
                user.getId(), userMission.getMissionList().getTitle(), rewardPoints);
    }
}
