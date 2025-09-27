package com.olympus.uga.domain.mission.domain.repo;

import com.olympus.uga.domain.mission.domain.MissionList;
import com.olympus.uga.domain.mission.domain.enums.MissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionListJpaRepo extends JpaRepository<MissionList, Long> {
    List<MissionList> findByMissionTypeAndIsEnabledTrue(MissionType missionType);

    @Query(value = "SELECT * FROM tb_mission_list WHERE mission_type = :missionType AND is_enabled = true ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<MissionList> findRandomMissions(@Param("missionType") String missionType, @Param("limit") int limit); // limit: 가져올 미션 개수
}
