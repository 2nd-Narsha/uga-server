package com.olympus.uga.domain.uga.domain.repo;

import com.olympus.uga.domain.uga.domain.Uga;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UgaJpaRepo extends JpaRepository<Uga, Long> {
    List<Uga> findByFamilyCodeAndGrowth(String familyCode, UgaGrowth growth);

}
