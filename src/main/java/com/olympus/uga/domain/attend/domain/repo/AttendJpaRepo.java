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
    Optional<Attend> findByUser_Id(Long userId);

    @Modifying
    @Query("DELETE FROM Attend a WHERE a.user.id = :userId")
    void deleteAllByUser_Id(@Param("userId") Long userId);
}
