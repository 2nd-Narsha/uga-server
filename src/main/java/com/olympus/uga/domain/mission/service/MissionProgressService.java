package com.olympus.uga.domain.mission.service;

import com.olympus.uga.domain.mission.domain.UserMission;
import com.olympus.uga.domain.mission.domain.enums.ActionType;
import com.olympus.uga.domain.mission.domain.enums.MissionType;
import com.olympus.uga.domain.mission.domain.enums.StatusType;
import com.olympus.uga.domain.mission.domain.repo.UserMissionJpaRepo;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionProgressService {
    private final UserMissionJpaRepo userMissionJpaRepo;
    private final UserSessionHolder userSessionHolder;

    @Transactional
    public void updateMissionProgress(User user, ActionType actionType) {
        List<UserMission> incompleteMissions = findIncompleteMissionsByAction(user, actionType);

        for (UserMission userMission : incompleteMissions) {
            incrementAndCheckCompletion(userMission, user);
        }
    }

    @Transactional
    public void updateCurrentUserMissionProgress(ActionType actionType) {
        User user = userSessionHolder.getUser();
        updateMissionProgress(user, actionType);
    }

    @Transactional
    public void onLetterSent(User user) {
        updateMissionProgress(user, ActionType.LETTER_SEND);
    }

    @Transactional
    public void onMemoCreated(User user) {
        updateMissionProgress(user, ActionType.MEMO_CREATE);
    }

    @Transactional
    public void onQuestionCreated(User user) {
        updateMissionProgress(user, ActionType.QUESTION_CREATE);
    }

    @Transactional
    public void onQuestionAnswered(User user) {
        updateMissionProgress(user, ActionType.QUESTION_ANSWER);
    }

    @Transactional
    public void onAlbumUploaded(User user) {
        updateMissionProgress(user, ActionType.ALBUM_UPLOAD);
    }

    @Transactional
    public void onAlbumCommented(User user) {
        updateMissionProgress(user, ActionType.ALBUM_COMMENT);
    }

    @Transactional
    public void onUgaFeed(User user) {
        updateMissionProgress(user, ActionType.UGA_FEED);
    }

    @Transactional
    public void onUgaItemBought(User user) {
        updateMissionProgress(user, ActionType.UGA_ITEM_BUY);
    }

    @Transactional
    public void onScheduleCreated(User user) {
        updateMissionProgress(user, ActionType.SCHEDULE_CREATE);
    }

    @Transactional
    public void onLetterDetailSent(User user) {
        updateMissionProgress(user, ActionType.LETTER_DETAIL_SEND);
    }

    private List<UserMission> findIncompleteMissionsByAction(User user, ActionType actionType) {
        return userMissionJpaRepo.findByUser(user).stream()
                .filter(um -> um.getStatus() == StatusType.INCOMPLETE)
                .filter(um -> um.getMissionList().getActionType() == actionType)
                .toList();
    }

    private void incrementAndCheckCompletion(UserMission userMission, User user) {
        userMission.incrementCount();
        userMissionJpaRepo.save(userMission);

        if (isTargetReached(userMission)) {
            completeMission(userMission, user);
        }
    }

    private boolean isTargetReached(UserMission userMission) {
        return userMission.getCurrentCount() >= userMission.getMissionList().getTargetCount();
    }

    private void completeMission(UserMission userMission, User user) {
        userMission.completeTask();
        userMissionJpaRepo.save(userMission);

        log.info("미션 완료: 사용자 {}, 미션 '{}' ({})",
                user.getId(),
                userMission.getMissionList().getTitle(),
                userMission.getMissionList().getActionType());

        // 일일 미션 완료 시 일일 보너스 미션 상태 확인 및 업데이트
        if (userMission.getMissionList().getMissionType() == MissionType.DAILY) {
            checkAndUpdateDailyBonus(user);
        }
    }

    private void checkAndUpdateDailyBonus(User user) {
        // 사용자의 모든 일일 미션 조회
        List<UserMission> dailyMissions = userMissionJpaRepo.findByUserAndMissionType(user, MissionType.DAILY);

        // 모든 일일 미션이 완료되었는지 확인
        boolean allDailyMissionsCompleted = dailyMissions.stream()
                .allMatch(um -> um.getStatus() == StatusType.WAITING_REWARD || um.getStatus() == StatusType.COMPLETED);

        if (allDailyMissionsCompleted && dailyMissions.size() >= 3) {
            // 일일 보너스 미션 조회 및 상태 업데이트
            userMissionJpaRepo.findDailyBonusByUser(user).ifPresent(dailyBonus -> {
                if (dailyBonus.getStatus() == StatusType.INCOMPLETE) {
                    dailyBonus.completeTask();
                    userMissionJpaRepo.save(dailyBonus);

                    log.info("일일 보너스 미션 활성화: 사용자 {}", user.getId());
                }
            });
        }
    }
}
