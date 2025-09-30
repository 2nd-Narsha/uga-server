package com.olympus.uga.domain.mission.service;

import com.olympus.uga.domain.mission.domain.UserMission;
import com.olympus.uga.domain.mission.domain.enums.MissionType;
import com.olympus.uga.domain.mission.domain.enums.StatusType;
import com.olympus.uga.domain.mission.domain.repo.UserMissionJpaRepo;
import com.olympus.uga.domain.mission.presentation.dto.response.MissionListRes;
import com.olympus.uga.domain.mission.presentation.dto.response.MissionRes;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.global.common.ResponseData;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionQueryService {
    private final UserMissionJpaRepo userMissionJpaRepo;
    private final UserSessionHolder userSessionHolder;

    @Transactional(readOnly = true)
    public ResponseData<MissionListRes> getMissionList() {
        User user = userSessionHolder.getUser();
        List<UserMission> userMissions = userMissionJpaRepo.findByUser(user);

        List<MissionRes> dailyMissionRes = filterMissionsByType(userMissions, MissionType.DAILY);
        List<MissionRes> weeklyMissionRes = filterMissionsByType(userMissions, MissionType.WEEKLY);
        MissionListRes.DailyBonusInfo dailyBonusInfo = getDailyBonusInfo(user, dailyMissionRes);

        return ResponseData.ok("미션 목록을 성공적으로 가져왔습니다.",
                MissionListRes.of(dailyMissionRes, weeklyMissionRes, dailyBonusInfo));
    }

    private List<MissionRes> filterMissionsByType(List<UserMission> userMissions, MissionType type) {
        return userMissions.stream()
                .filter(um -> um.getMissionList().getMissionType() == type)
                .map(MissionRes::from)
                .toList();
    }

    private MissionListRes.DailyBonusInfo getDailyBonusInfo(User user, List<MissionRes> dailyMissions) {
        int completedCount = countCompletedMissions(dailyMissions);
        int totalCount = dailyMissions.size();
        String progressText = completedCount + "/" + totalCount;

        Optional<UserMission> dailyBonusOpt = userMissionJpaRepo.findDailyBonusByUser(user);

        boolean canClaimBonus = false;
        StatusType status = StatusType.INCOMPLETE;

        if (dailyBonusOpt.isPresent()) {
            UserMission dailyBonus = dailyBonusOpt.get();
            status = dailyBonus.getStatus();
            // 상태 변경 로직 제거: 조회만 수행
            canClaimBonus = status == StatusType.WAITING_REWARD;
        }

        return new MissionListRes.DailyBonusInfo(
                completedCount, totalCount, progressText, canClaimBonus, 500, status
        );
    }

    private int countCompletedMissions(List<MissionRes> missions) {
        return (int) missions.stream()
                .filter(mission -> mission.status() == StatusType.COMPLETED || mission.status() == StatusType.WAITING_REWARD)
                .count();
    }
}
