package com.olympus.uga.domain.mission.domain.repo;

import com.olympus.uga.domain.mission.domain.MissionList;
import com.olympus.uga.domain.mission.domain.UserMission;
import com.olympus.uga.domain.mission.domain.enums.MissionType;
import com.olympus.uga.domain.mission.domain.enums.StatusType;
import com.olympus.uga.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMissionJpaRepo extends JpaRepository<UserMission, Long> {
    List<UserMission> findByUser(User user);
    List<UserMission> findByUserAndStatus(User user, StatusType status);
    List<UserMission> findByUserAndMissionTemplateMissionType(User user, MissionType missionType);
    boolean existsByUserAndMissionTemplate(User user, MissionList missionList);

    @Query("SELECT um FROM UserMission um WHERE um.user = :user AND um.missionList.actionType = :actionType")
    Optional<UserMission> findByUserAndActionType(@Param("user") User user, @Param("actionType") String actionType);

    // 일일 보너스 미션 조회
    @Query("SELECT um FROM UserMission um WHERE um.user = :user AND um.missionList.missionType = 'DAILY_BONUS'")
    Optional<UserMission> findDailyBonusByUser(@Param("user") User user);
}
