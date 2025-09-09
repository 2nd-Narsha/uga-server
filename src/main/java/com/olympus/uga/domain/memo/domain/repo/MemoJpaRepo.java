package com.olympus.uga.domain.memo.domain.repo;

import com.olympus.uga.domain.memo.domain.Memo;
import com.olympus.uga.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface MemoJpaRepo extends JpaRepository<Memo, Long> {
    Optional<Memo> findByWriter(User user);

    @Modifying
    @Query("DELETE FROM Memo m WHERE m.familyCode = :familyCode")
    void deleteByFamilyCode(@Param("familyCode") String familyCode);
}
