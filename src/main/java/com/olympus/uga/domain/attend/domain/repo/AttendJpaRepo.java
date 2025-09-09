package com.olympus.uga.domain.attend.domain.repo;

import com.olympus.uga.domain.attend.domain.Attend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface AttendJpaRepo extends JpaRepository<Attend, Long> {
    Optional<Attend> findByUserId(Long userId);

    @Query("DELETE FROM Attend a WHERE a.family.familyCode = :familyCode")
    @Modifying
    void deleteByFamilyCode(@Param("familyCode") String familyCode);
}
