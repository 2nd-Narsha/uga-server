package com.olympus.uga.domain.calendar.domain.repo;

import com.olympus.uga.domain.calendar.domain.ScheduleParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleParticipantJpaRepo extends JpaRepository<ScheduleParticipant, Long> {
    @Query("SELECT sp.userId FROM ScheduleParticipant sp WHERE sp.schedule.id = :scheduleId")
    List<Long> findUserIdsByScheduleId(@Param("scheduleId") Long scheduleId);
}