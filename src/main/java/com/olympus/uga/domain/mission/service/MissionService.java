package com.olympus.uga.domain.mission.service;

import com.olympus.uga.domain.mission.domain.MissionList;
import com.olympus.uga.domain.mission.domain.UserMission;
import com.olympus.uga.domain.mission.domain.enums.ActionType;
import com.olympus.uga.domain.mission.domain.enums.MissionType;
import com.olympus.uga.domain.mission.domain.enums.StatusType;
import com.olympus.uga.domain.mission.domain.repo.MissionListJpaRepo;
import com.olympus.uga.domain.mission.domain.repo.UserMissionJpaRepo;
import com.olympus.uga.domain.mission.error.MissionErrorCode;
import com.olympus.uga.domain.mission.presentation.dto.response.MissionListRes;
import com.olympus.uga.domain.mission.presentation.dto.response.MissionRes;
import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.repo.UserJpaRepo;
import com.olympus.uga.global.common.Response;
import com.olympus.uga.global.common.ResponseData;
import com.olympus.uga.global.exception.CustomException;
import com.olympus.uga.global.security.auth.UserSessionHolder;
import com.olympus.uga.global.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionListJpaRepo missionListJpaRepo;
    private final UserMissionJpaRepo userMissionJpaRepo;
    private final UserJpaRepo userJpaRepo;
    private final UserSessionHolder userSessionHolder;
    private final WebSocketService webSocketService;

    @Transactional(readOnly = true)
    public ResponseData<MissionListRes> getMissionList() {
        User user = userSessionHolder.getUser();
        List<UserMission> userMissions = userMissionJpaRepo.findByUser(user);

        // 일일/주간 미션으로 분류
        List<MissionRes> dailyMissionRes = userMissions.stream()
                .filter(um -> um.getMissionList().getMissionType() == MissionType.DAILY)
                .map(MissionRes::from)
                .toList();

        List<MissionRes> weeklyMissionRes = userMissions.stream()
                .filter(um -> um.getMissionList().getMissionType() == MissionType.WEEKLY)
                .map(MissionRes::from)
                .toList();

        // 일일 미션 완료 보너스 정보
        MissionListRes.DailyBonusInfo dailyBonusInfo = getDailyBonusInfo(user, dailyMissionRes);

        return ResponseData.ok("미션 목록을 성공적으로 가져왔습니다.",
                MissionListRes.of(dailyMissionRes, weeklyMissionRes, dailyBonusInfo));
    }

    private MissionListRes.DailyBonusInfo getDailyBonusInfo(User user, List<MissionRes> dailyMissions) {
        int completedCount = (int) dailyMissions.stream()
                .filter(mission -> mission.status() == StatusType.COMPLETED)
                .count();
        int totalCount = dailyMissions.size();
        String progressText = completedCount + "/" + totalCount;

        // 일일 보너스 미션 조회
        Optional<UserMission> dailyBonusOpt = userMissionJpaRepo.findDailyBonusByUser(user);

        boolean canClaimBonus = false;
        StatusType status = StatusType.INCOMPLETE;

        if (dailyBonusOpt.isPresent()) {
            UserMission dailyBonus = dailyBonusOpt.get();
            // 일일 미션 3개 완료 시 보너스 활성화
            if (completedCount >= 3) {
                dailyBonus.setCurrentCount(3); // 목표 달성
                if (dailyBonus.getStatus() == StatusType.INCOMPLETE) {
                    dailyBonus.completeTask(); // WAITING_REWARD 상태로 변경
                    userMissionJpaRepo.save(dailyBonus);
                }
            }
            status = dailyBonus.getStatus();
            canClaimBonus = status == StatusType.WAITING_REWARD;
        }

        return new MissionListRes.DailyBonusInfo(
                completedCount, totalCount, progressText, canClaimBonus, 500, status
        );
    }

    @Transactional
    public Response claimReward(Long missionId) {
        User user = userSessionHolder.getUser();
        UserMission userMission = userMissionJpaRepo.findById(missionId)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MISSION_NOT_FOUND));

        if (!userMission.getUser().getId().equals(user.getId())) {
            throw new CustomException(MissionErrorCode.MISSION_ACCESS_DENIED);
        }

        if (userMission.getStatus() != StatusType.WAITING_REWARD) {
            throw new CustomException(MissionErrorCode.REWARD_NOT_AVAILABLE);
        }

        // 보상 지급
        int rewardPoints = userMission.getMissionList().getRewardPoints();
        user.earnPoint(rewardPoints);
        userMission.claimReward();
        user.updateLastActivityAt();
        userJpaRepo.save(user);

        // 포인트 변경 웹소켓 알림
        webSocketService.notifyPointUpdate(user.getId(), user.getPoint(), "미션 완료 보상");

        return Response.ok("보상을 성공적으로 받았습니다.");
    }

    @Transactional
    public Response claimDailyBonus() {
        User user = userSessionHolder.getUser();

        // 일일 보너스 미션 조회
        UserMission dailyBonus = userMissionJpaRepo.findDailyBonusByUser(user)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MISSION_NOT_FOUND));

        if (dailyBonus.getStatus() != StatusType.WAITING_REWARD) {
            throw new CustomException(MissionErrorCode.REWARD_NOT_AVAILABLE);
        }

        // 보상 지급
        int rewardPoints = dailyBonus.getMissionList().getRewardPoints();
        user.earnPoint(rewardPoints);
        dailyBonus.claimReward();
        user.updateLastActivityAt();
        userJpaRepo.save(user);

        // 포인트 변경 웹소켓 알림
        webSocketService.notifyPointUpdate(user.getId(), user.getPoint(), "일일 미션 완료 보너스");

        return Response.ok("일일 미션 완료 보너스를 받았습니다!");
    }

    // 미션 진행도 업데이트 (다른 서비스에서 호출)
    @Transactional
    public void updateMissionProgress(User user, ActionType actionType) {
        // 해당 actionType의 미션들을 모두 업데이트
        List<UserMission> userMissions = userMissionJpaRepo.findByUser(user).stream()
                .filter(um -> um.getStatus() == StatusType.INCOMPLETE)
                .filter(um -> um.getMissionList().getActionType() == actionType)
                .toList();

        for (UserMission userMission : userMissions) {
            userMission.incrementCount();
            userMissionJpaRepo.save(userMission);

            // 미션 완료 체크
            if (userMission.getCurrentCount() >= userMission.getMissionList().getTargetCount()) {
                userMission.completeTask(); // WAITING_REWARD 상태로 변경
                userMissionJpaRepo.save(userMission);
                log.info("미션 완료: 사용자 {}, 미션 '{}' ({})",
                        user.getId(), userMission.getMissionList().getTitle(), actionType);
            }
        }
    }

    // 현재 로그인한 사용자의 미션 진행도 업데이트 (편의 메서드)
    @Transactional
    public void updateCurrentUserMissionProgress(ActionType actionType) {
        User user = userSessionHolder.getUser();
        updateMissionProgress(user, actionType);
    }

    // 다른 서비스에서 사용할 수 있는 편의 메서드들
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

    // 현재 로그인한 사용자 기준 편의 메서드들
    @Transactional
    public void onLetterSent() {
        updateCurrentUserMissionProgress(ActionType.LETTER_SEND);
    }

    @Transactional
    public void onMemoCreated() {
        updateCurrentUserMissionProgress(ActionType.MEMO_CREATE);
    }

    @Transactional
    public void onQuestionCreated() {
        updateCurrentUserMissionProgress(ActionType.QUESTION_CREATE);
    }

    @Transactional
    public void onQuestionAnswered() {
        updateCurrentUserMissionProgress(ActionType.QUESTION_ANSWER);
    }

    @Transactional
    public void onAlbumUploaded() {
        updateCurrentUserMissionProgress(ActionType.ALBUM_UPLOAD);
    }

    @Transactional
    public void onAlbumCommented() {
        updateCurrentUserMissionProgress(ActionType.ALBUM_COMMENT);
    }

    @Transactional
    public void onUgaFeed() {
        updateCurrentUserMissionProgress(ActionType.UGA_FEED);
    }

    @Transactional
    public void onUgaItemBought() {
        updateCurrentUserMissionProgress(ActionType.UGA_ITEM_BUY);
    }

    @Transactional
    public void onScheduleCreated() {
        updateCurrentUserMissionProgress(ActionType.SCHEDULE_CREATE);
    }

    // 미션 할당 로직 (스케줄러에서 호출)
    @Transactional
    public void assignDailyMissions() {
        log.info("일일 미션 갱신 시작");
        List<User> allUsers = userJpaRepo.findAll();
        List<MissionList> dailyMissions = missionListJpaRepo.findByMissionTypeAndIsEnabledTrue(MissionType.DAILY);

        if (dailyMissions.size() < 3) {
            log.warn("일일 미션 템플릿이 부족합니다. 현재: {}개", dailyMissions.size());
            return;
        }

        for (User user : allUsers) {
            // 기존 일일 미션 삭제 (보너스 미션 제외)
            userMissionJpaRepo.deleteUserDailyMissions(user);

            // 랜덤 3개 일일 미션 할당
            List<MissionList> randomMissions = missionListJpaRepo.findRandomMissions("DAILY", 3);
            for (MissionList mission : randomMissions) {
                UserMission userMission = UserMission.assign(user, mission);
                userMissionJpaRepo.save(userMission);
            }

            // 일일 보너스 미션 할당 (없는 경우에만)
            if (userMissionJpaRepo.findDailyBonusByUser(user).isEmpty()) {
                List<MissionList> bonusMissions = missionListJpaRepo.findDailyBonusMissions();
                if (!bonusMissions.isEmpty()) {
                    UserMission bonusUserMission = UserMission.assign(user, bonusMissions.get(0));
                    userMissionJpaRepo.save(bonusUserMission);
                }
            }
        }
        log.info("일일 미션 갱신 완료");
    }

    @Transactional
    public void assignWeeklyMissions() {
        log.info("주간 미션 갱신 시작");
        List<User> allUsers = userJpaRepo.findAll();
        List<MissionList> weeklyMissions = missionListJpaRepo.findByMissionTypeAndIsEnabledTrue(MissionType.WEEKLY);

        for (User user : allUsers) {
            // 기존 주간 미션 삭제
            userMissionJpaRepo.deleteUserWeeklyMissions(user);

            // 모든 주간 미션 할당
            for (MissionList mission : weeklyMissions) {
                UserMission userMission = UserMission.assign(user, mission);
                userMissionJpaRepo.save(userMission);
            }
        }
        log.info("주간 미션 갱신 완료");
    }

    // 사용자가 처음 가입할 때 미션 할당
    @Transactional
    public void assignInitialMissionsForNewUser(User user) {
        // 일일 미션 3개 랜덤 할당
        List<MissionList> randomDailyMissions = missionListJpaRepo.findRandomMissions("DAILY", 3);
        for (MissionList mission : randomDailyMissions) {
            UserMission userMission = UserMission.assign(user, mission);
            userMissionJpaRepo.save(userMission);
        }

        // 일일 보너스 미션 할당
        List<MissionList> bonusMissions = missionListJpaRepo.findDailyBonusMissions();
        if (!bonusMissions.isEmpty()) {
            UserMission bonusUserMission = UserMission.assign(user, bonusMissions.get(0));
            userMissionJpaRepo.save(bonusUserMission);
        }

        // 모든 주간 미션 할당
        List<MissionList> weeklyMissions = missionListJpaRepo.findByMissionTypeAndIsEnabledTrue(MissionType.WEEKLY);
        for (MissionList mission : weeklyMissions) {
            UserMission userMission = UserMission.assign(user, mission);
            userMissionJpaRepo.save(userMission);
        }

        log.info("신규 사용자 {}에게 초기 미션 할당 완료", user.getId());
    }

    // 현재 로그인한 사용자에게 미션 할당 (테스트용)
    @Transactional
    public Response assignMissionsToCurrentUser() {
        User user = userSessionHolder.getUser();
        assignInitialMissionsForNewUser(user);
        return Response.ok("미션이 성공적으로 할당되었습니다.");
    }
}
