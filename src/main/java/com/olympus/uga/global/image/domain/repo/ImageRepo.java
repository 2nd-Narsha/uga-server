package com.olympus.uga.global.image.domain.repo;

import com.olympus.uga.global.image.domain.ImageDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepo extends JpaRepository<ImageDetails, Long> {
    Optional<ImageDetails> findByImageName(String fileName);
    Optional<ImageDetails> findByFileName(String fileName);
}