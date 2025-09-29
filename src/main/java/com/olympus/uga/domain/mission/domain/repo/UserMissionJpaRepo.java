package com.olympus.uga.domain.mission.domain.repo;

import com.olympus.uga.domain.mission.domain.UserMission;
import com.olympus.uga.domain.mission.domain.enums.MissionType;
import com.olympus.uga.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserMissionJpaRepo extends JpaRepository<UserMission, Long> {
    List<UserMission> findByUser(User user);

    // 사용자별 특정 타입 미션 목록 조회
    @Query("SELECT um FROM UserMission um JOIN um.missionList ml WHERE um.user = :user AND ml.missionType = :missionType")
    List<UserMission> findByUserAndMissionType(@Param("user") User user, @Param("missionType") MissionType missionType);

    // 일일 보너스 미션 조회
    @Query("SELECT um FROM UserMission um JOIN um.missionList ml WHERE um.user = :user AND ml.missionType = 'DAILY_BONUS'")
    Optional<UserMission> findDailyBonusByUser(@Param("user") User user);

    // 사용자의 기존 일일 미션 삭제 (보너스 미션 제외)
    @Modifying
    @Query("DELETE FROM UserMission um WHERE um.user = :user AND um.missionList.missionType = 'DAILY'")
    void deleteUserDailyMissions(@Param("user") User user);

    // 사용자의 기존 주간 미션 삭제
    @Modifying
    @Query("DELETE FROM UserMission um WHERE um.user = :user AND um.missionList.missionType = 'WEEKLY'")
    void deleteUserWeeklyMissions(@Param("user") User user);
}
