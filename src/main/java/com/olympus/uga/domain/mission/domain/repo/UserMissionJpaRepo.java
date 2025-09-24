package com.olympus.uga.domain.mission.domain.repo;

import com.olympus.uga.domain.mission.domain.MissionList;
import com.olympus.uga.domain.mission.domain.UserMission;
import com.olympus.uga.domain.mission.domain.enums.StatusType;
import com.olympus.uga.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMissionJpaRepo extends JpaRepository<UserMission, Long> {
    List<UserMission> findByUser(User user);
    List<UserMission> findByUserAndStatus(User user, StatusType status);
    boolean existsByUserAndMissionList(User user, MissionList missionList);
}
