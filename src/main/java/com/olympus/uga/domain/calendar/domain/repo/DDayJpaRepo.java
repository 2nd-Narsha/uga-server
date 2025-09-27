package com.olympus.uga.domain.calendar.domain.repo;

import com.olympus.uga.domain.calendar.domain.DDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DDayJpaRepo extends JpaRepository<DDay, Long> {
    List<DDay> findByFamilyCodeOrderByDateAsc(String familyCode);
    void deleteByFamilyCode(String familyCode);
    List<DDay> findByDateBefore(LocalDate date);

    // 특정 날짜에 시작시간이 설정된 디데이 목록 조회 (알림용)
    @Query("SELECT d FROM DDay d WHERE d.date = :targetDate " +
           "AND d.startTime IS NOT NULL " +
           "AND d.isNotificationSent = false")
    List<DDay> findByDateAndStartTimeNotNullAndIsNotificationSentFalse(@Param("targetDate") LocalDate targetDate);
}
