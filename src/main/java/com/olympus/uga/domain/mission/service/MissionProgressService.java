package com.olympus.uga.domain.mission.service;

import com.olympus.uga.domain.mission.domain.UserMission;
import com.olympus.uga.domain.mission.domain.enums.ActionType;
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
    }
}
