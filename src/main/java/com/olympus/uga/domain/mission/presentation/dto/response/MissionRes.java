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
        boolean canClaimReward,
        String progressText  // "현재진행수/목표수" 형태로 표시
) {
    public static MissionRes from(UserMission userMission) {
        int currentCount = userMission.getCurrentCount();
        int targetCount = userMission.getMissionList().getTargetCount();
        String progressText = currentCount + "/" + targetCount;

        return new MissionRes(
                userMission.getId(),
                userMission.getMissionList().getTitle(),
                userMission.getMissionList().getRewardPoints(),
                userMission.getStatus(),
                userMission.getMissionList().getMissionType(),
                userMission.getStatus() == StatusType.WAITING_REWARD,
                progressText
        );
    }
}