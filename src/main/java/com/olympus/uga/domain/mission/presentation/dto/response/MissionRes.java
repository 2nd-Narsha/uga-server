package com.olympus.uga.domain.mission.presentation.dto.response;

import com.olympus.uga.domain.mission.domain.UserMission;
import com.olympus.uga.domain.mission.domain.enums.MissionType;
import com.olympus.uga.domain.mission.domain.enums.StatusType;

public record MissionRes(
        Long id,
        String title,
        int rewardPoints,
        StatusType status,
        MissionType missionType,
        String progressText,  // "1 / 3"
        boolean canClaimReward
) {
    public static MissionRes from(UserMission userMission) {
        return new MissionRes(
                userMission.getId(),
                userMission.getMissionList().getTitle(),
                userMission.getMissionList().getRewardPoints(),
                userMission.getStatus(),
                userMission.getMissionList().getMissionType(),
                userMission.getProgressText(),
                userMission.getStatus() == StatusType.WAITING_REWARD
        );
    }
}