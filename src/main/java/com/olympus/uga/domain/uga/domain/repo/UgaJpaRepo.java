package com.olympus.uga.domain.uga.domain.repo;

import com.olympus.uga.domain.uga.domain.Uga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UgaJpaRepo extends JpaRepository<Uga, Long> {
}
