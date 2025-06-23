package com.olympus.uga.domain.uga.domain.repo;

import com.olympus.uga.domain.uga.domain.UgaContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UgaContributionJpaRepo extends JpaRepository<UgaContribution, Long> {
    Optional<UgaContribution> findByUgaIdAndUserId(Long ugaId, Long userId);

    @Query("SELECT SUM(uc.contributedDays) FROM UgaContribution uc WHERE uc.ugaId = :ugaId")
    Integer getTotalContributedDaysByUgaId(@Param("ugaId") Long ugaId);
}
