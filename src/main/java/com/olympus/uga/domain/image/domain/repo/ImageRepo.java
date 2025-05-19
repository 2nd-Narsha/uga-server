package com.olympus.uga.domain.image.domain.repo;

import com.olympus.uga.domain.image.domain.ImageDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepo extends JpaRepository<ImageDetails, Long> {
    Optional<ImageDetails> findByFileName(String fileName);
}
