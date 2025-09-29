package com.olympus.uga.domain.mission.service;

import com.olympus.uga.domain.mission.domain.MissionList;
import com.olympus.uga.domain.mission.domain.UserMission;
import com.olympus.uga.domain.mission.domain.enums.MissionType;
import com.olympus.uga.domain.mission.domain.repo.MissionListJpaRepo;
import com.olympus.uga.domain.mission.domain.repo.UserMissionJpaRepo;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionAssignService {
    private final MissionListJpaRepo missionListJpaRepo;
    private final UserMissionJpaRepo userMissionJpaRepo;
    private final UserJpaRepo userJpaRepo;

    private static final int DAILY_MISSION_COUNT = 3;

    @Transactional
    public void assignDailyMissions() {
        log.info("일일 미션 갱신 시작");

        List<MissionList> dailyMissions = missionListJpaRepo.findByMissionTypeAndIsEnabledTrue(MissionType.DAILY);
        if (!validateDailyMissionTemplate(dailyMissions)) {
            return;
        }

        List<User> allUsers = userJpaRepo.findAll();
        for (User user : allUsers) {
            assignDailyMissionsToUser(user);
        }

        log.info("일일 미션 갱신 완료");
    }

    @Transactional
    public void assignWeeklyMissions() {
        log.info("주간 미션 갱신 시작");

        List<MissionList> weeklyMissions = missionListJpaRepo.findByMissionTypeAndIsEnabledTrue(MissionType.WEEKLY);
        List<User> allUsers = userJpaRepo.findAll();

        for (User user : allUsers) {
            assignWeeklyMissionsToUser(user, weeklyMissions);
        }

        log.info("주간 미션 갱신 완료");
    }

    @Transactional
    public void assignMissionsToNewUser(User user) {
        log.info("신규 사용자 미션 할당: {}", user.getId());

        assignDailyMissionsToUser(user);
        assignWeeklyMissionsToUser(user, getEnabledWeeklyMissions());
    }

    private boolean validateDailyMissionTemplate(List<MissionList> dailyMissions) {
        if (dailyMissions.size() < DAILY_MISSION_COUNT) {
            log.warn("일일 미션 템플릿이 부족합니다. 현재: {}개", dailyMissions.size());
            return false;
        }
        return true;
    }

    private void assignDailyMissionsToUser(User user) {
        userMissionJpaRepo.deleteUserDailyMissions(user);

        List<MissionList> randomMissions = missionListJpaRepo.findRandomMissions("DAILY", DAILY_MISSION_COUNT);
        assignMissions(user, randomMissions);

        assignDailyBonusMissionIfNeeded(user);
    }

    private void assignDailyBonusMissionIfNeeded(User user) {
        if (userMissionJpaRepo.findDailyBonusByUser(user).isEmpty()) {
            List<MissionList> bonusMissions = missionListJpaRepo.findDailyBonusMissions();
            if (!bonusMissions.isEmpty()) {
                assignMission(user, bonusMissions.get(0));
            }
        }
    }

    private void assignWeeklyMissionsToUser(User user, List<MissionList> weeklyMissions) {
        userMissionJpaRepo.deleteUserWeeklyMissions(user);
        assignMissions(user, weeklyMissions);
    }

    private List<MissionList> getEnabledWeeklyMissions() {
        return missionListJpaRepo.findByMissionTypeAndIsEnabledTrue(MissionType.WEEKLY);
    }

    private void assignMissions(User user, List<MissionList> missions) {
        for (MissionList mission : missions) {
            assignMission(user, mission);
        }
    }

    private void assignMission(User user, MissionList mission) {
        UserMission userMission = UserMission.assign(user, mission);
        userMissionJpaRepo.save(userMission);
    }
}