package com.olympus.uga.global.image.domain.repo;

import com.olympus.uga.global.image.domain.ImageDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepo extends JpaRepository<ImageDetails, Integer> {
    Optional<ImageDetails> findByImageName(String imageName);  // fileName 메서드 삭제, imageName으로 통일
}