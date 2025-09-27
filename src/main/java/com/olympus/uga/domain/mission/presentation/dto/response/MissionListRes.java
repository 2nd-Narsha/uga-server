package com.olympus.uga.domain.mission.presentation.dto.response;

import com.olympus.uga.domain.mission.domain.enums.StatusType;

import java.util.List;

public record MissionListRes(
        List<MissionRes> dailyMissions,
        List<MissionRes> weeklyMissions,
        DailyBonusInfo dailyBonusInfo
) {
    public static MissionListRes of(List<MissionRes> dailyMissions, List<MissionRes> weeklyMissions, DailyBonusInfo dailyBonusInfo) {
        return new MissionListRes(dailyMissions, weeklyMissions, dailyBonusInfo);
    }

    public record DailyBonusInfo(
            int completedCount,
            int totalCount,
            String progressText,  // "1/3"
            boolean canClaimBonus,
            int bonusPoints,
            StatusType status
    ) {}
}
