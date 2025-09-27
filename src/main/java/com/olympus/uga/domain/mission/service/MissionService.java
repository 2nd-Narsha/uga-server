package com.olympus.uga.domain.mission.service;

import com.olympus.uga.domain.mission.domain.MissionList;
import com.olympus.uga.domain.mission.domain.UserMission;
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
    public void updateMissionProgress(User user, String actionType) {
        userMissionJpaRepo.findByUserAndActionType(user, actionType)
                .ifPresent(userMission -> {
                    if (userMission.getStatus() == StatusType.INCOMPLETE) {
                        userMission.incrementCount();
                        userMissionJpaRepo.save(userMission);
                    }
                });
    }

    // 미션 할당 로직 (스케줄러에서 호출)
    @Transactional
    public void assignDailyMissions() {
        log.info("일일 미션 갱신 시작");
        List<User> allUsers = userJpaRepo.findAll();
        List<MissionList> randomDailyMissions = missionListJpaRepo.findRandomMissions("DAILY", 3);

        if (randomDailyMissions.size() < 3) {
            log.warn("일일 미션 템플릿이 부족합니다. 현재: {}개", randomDailyMissions.size());
            return;
        }

        for (User user : allUsers) {
            // 기존 일일 미션 삭제 (보너스 미션 제외)
            List<UserMission> existingDailyMissions = userMissionJpaRepo.findByUser(user).stream()
                    .filter(um -> um.getMissionList().getMissionType() == MissionType.DAILY)
                    .toList();
            userMissionJpaRepo.deleteAll(existingDailyMissions);

            // 새로운 일일 미션 할당
            for (MissionList missionTemplate : randomDailyMissions) {
                UserMission userMission = UserMission.builder()
                        .user(user)
                        .missionList(missionTemplate)
                        .status(StatusType.INCOMPLETE)
                        .currentCount(0)
                        .build();
                userMissionJpaRepo.save(userMission);
            }

            // 일일 보너스 미션 초기화 (이미 있으면 currentCount만 0으로)
            Optional<UserMission> dailyBonusOpt = userMissionJpaRepo.findDailyBonusByUser(user);
            if (dailyBonusOpt.isPresent()) {
                UserMission dailyBonus = dailyBonusOpt.get();
                dailyBonus.resetProgress(); // currentCount = 0, status = INCOMPLETE
                userMissionJpaRepo.save(dailyBonus);
            } else {
                // 보너스 미션이 없으면 생성 (최초 한번만)
                createDailyBonusMission(user);
            }
        }
        log.info("일일 미션 갱신 완료");
    }

    private void createDailyBonusMission(User user) {
        // 일일 보너스 미션 템플릿이 없으면 생성
        MissionList bonusTemplate = missionListJpaRepo.findByMissionTypeAndIsEnabledTrue(MissionType.DAILY_BONUS)
                .stream().findFirst()
                .orElseGet(() -> {
                    // 보너스 템플릿이 없으면 기본 생성
                    return MissionList.builder()
                            .title("일일 미션 완료하기")
                            .missionType(MissionType.DAILY_BONUS)
                            .rewardPoints(500)
                            .targetCount(3)
                            .actionType("DAILY_COMPLETE")
                            .isEnabled(true)
                            .build();
                });

        UserMission bonusMission = UserMission.builder()
                .user(user)
                .missionList(bonusTemplate)
                .status(StatusType.INCOMPLETE)
                .currentCount(0)
                .build();
        userMissionJpaRepo.save(bonusMission);
    }

    @Transactional
    public void assignWeeklyMissions() {
        log.info("주간 미션 갱신 시작");
        List<User> allUsers = userJpaRepo.findAll();
        List<MissionList> randomWeeklyMissions = missionListJpaRepo.findRandomMissions("WEEKLY", 5);

        if (randomWeeklyMissions.size() < 5) {
            log.warn("주간 미션 템플릿이 부족합니다. 현재: {}개", randomWeeklyMissions.size());
            return;
        }

        for (User user : allUsers) {
            // 기존 주간 미션 삭제
            List<UserMission> existingWeeklyMissions = userMissionJpaRepo.findByUser(user).stream()
                    .filter(um -> um.getMissionList().getMissionType() == MissionType.WEEKLY)
                    .toList();
            userMissionJpaRepo.deleteAll(existingWeeklyMissions);

            // 새로운 주간 미션 할당
            for (MissionList missionTemplate : randomWeeklyMissions) {
                UserMission userMission = UserMission.builder()
                        .user(user)
                        .missionList(missionTemplate)
                        .status(StatusType.INCOMPLETE)
                        .currentCount(0)
                        .build();
                userMissionJpaRepo.save(userMission);
            }
        }
        log.info("주간 미션 갱신 완료");
    }
}