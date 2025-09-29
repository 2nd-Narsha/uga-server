package com.olympus.uga.domain.mission.domain.repo;

import com.olympus.uga.domain.mission.domain.MissionList;
import com.olympus.uga.domain.mission.domain.enums.MissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MissionListJpaRepo extends JpaRepository<MissionList, Long> {

    // 활성화된 미션 목록 조회
    List<MissionList> findByIsEnabledTrue();

    // 미션 타입별 활성화된 미션 목록 조회
    List<MissionList> findByMissionTypeAndIsEnabledTrue(MissionType missionType);

    // 랜덤 미션 조회 (미션 할당용)
    @Query(value = "SELECT * FROM tb_mission_list WHERE mission_type = :missionType AND is_enabled = true ORDER BY RAND() LIMIT :count",
           nativeQuery = true)
    List<MissionList> findRandomMissions(@Param("missionType") String missionType, @Param("count") int count);

    // 일일 보너스 미션 조회
    @Query("SELECT m FROM MissionList m WHERE m.missionType = 'DAILY_BONUS' AND m.isEnabled = true")
    List<MissionList> findDailyBonusMissions();
}
