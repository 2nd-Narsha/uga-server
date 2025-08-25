package com.olympus.uga.domain.album.domain.repo;

import com.olympus.uga.domain.album.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostJpaRepo extends JpaRepository<Post, Long> {
    List<Post> findByFamilyCodeOrderByCreatedAtDesc(String familyCode);
    Optional<Post> findByIdAndFamilyCode(Long id, String familyCode);
}
