package com.olympus.uga.domain.uga.domain.repo;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.uga.domain.Uga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UgaRepo extends JpaRepository<Uga, Long> {
    List<Uga> findByFamily(Family family);
}
