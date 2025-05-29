package com.olympus.uga.domain.family.domain.repo;

import com.olympus.uga.domain.family.domain.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamilyJpaRepo extends JpaRepository<Family, String> {
    // 특정 사용자가 속한 가족 조회
    @Query("SELECT f FROM Family f JOIN f.memberList m WHERE m = :userId")
    Optional<Family> findByMemberListContaining(@Param("userId") Long userId);
}
