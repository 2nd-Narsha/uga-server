package com.olympus.uga.domain.attend.domain.repo;

import com.olympus.uga.domain.attend.domain.Attend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendJpaRepo extends JpaRepository<Attend, Long> {
    Optional<Attend> findByUserId(Long userId);
}
