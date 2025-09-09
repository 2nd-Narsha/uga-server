package com.olympus.uga.domain.calendar.domain.repo;

import com.olympus.uga.domain.calendar.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleJpaRepo extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByIdAndFamilyCode(Long id, String familyCode);
    List<Schedule> findByFamilyCodeOrderByDateAscStartTimeAsc(String familyCode);
    List<Schedule> findByFamilyCodeAndDateOrderByStartTimeAsc(String familyCode, LocalDate date);
    void deleteByFamilyCode(String familyCode);
}
