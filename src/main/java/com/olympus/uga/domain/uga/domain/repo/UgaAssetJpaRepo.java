package com.olympus.uga.domain.uga.domain.repo;

import com.olympus.uga.domain.uga.domain.UgaAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UgaAssetJpaRepo extends JpaRepository<UgaAsset, String> {
}
