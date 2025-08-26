package com.olympus.uga.domain.album.domain.repo;

import com.olympus.uga.domain.album.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostJpaRepo extends JpaRepository<Post, Long> {
    List<Post> findByFamilyCodeOrderByCreatedAtDesc(String familyCode);
    Optional<Post> findByIdAndFamilyCode(Long id, String familyCode);

    @Query("SELECT DISTINCT p FROM Post p " +
            "WHERE p.writer.family.familyCode = :familyCode AND " +
            "EXISTS (SELECT 1 FROM PostImage pi WHERE pi.post = p) " +
            "ORDER BY p.createdAt DESC")
    List<Post> findPostsWithImagesByFamilyCode(@Param("familyCode") String familyCode);
}
