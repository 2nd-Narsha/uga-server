package com.olympus.uga.domain.calendar.domain.repo;

import com.olympus.uga.domain.calendar.domain.Dday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DdayJpaRepo extends JpaRepository<Dday, Long> {
    List<Dday> findByFamilyCodeOrderByDateAsc(String familyCode);
    Optional<Dday> findByIdAndFamilyCode(Long id, String familyCode);
    List<Dday> findByDateBefore(LocalDate date);
}
