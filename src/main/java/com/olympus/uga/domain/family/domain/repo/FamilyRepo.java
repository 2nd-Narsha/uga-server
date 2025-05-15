package com.olympus.uga.domain.family.domain.repo;

import com.olympus.uga.domain.family.domain.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamilyRepo extends JpaRepository<Family, Long> {
    Optional<Family> findByFamilyCode(String familyCode);
}
