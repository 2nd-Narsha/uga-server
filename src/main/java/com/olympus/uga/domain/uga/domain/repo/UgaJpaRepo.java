package com.olympus.uga.domain.uga.domain.repo;

import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UgaJpaRepo extends JpaRepository<Uga, Long> {
    List<Uga> findByFamilyCodeAndGrowth(String familyCode, UgaGrowth growth);
    List<Uga> findByGrowthNot(UgaGrowth growth);
    List<Uga> findByFamilyCode(String familyCode);

    @Modifying
    @Query("DELETE FROM Uga u WHERE u.familyCode = :familyCode")
    void deleteByFamilyCode(@Param("familyCode") String familyCode);
}
