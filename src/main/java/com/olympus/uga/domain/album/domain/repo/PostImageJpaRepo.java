package com.olympus.uga.domain.album.domain.repo;

import com.olympus.uga.domain.album.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageJpaRepo extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPostPostIdOrderByImageOrder(Long postId);
    void deleteByPostPostId(Long postId);

    @Modifying
    @Query("DELETE FROM PostImage pi WHERE pi.post.family.familyCode = :familyCode")
    void deleteByFamilyCode(@Param("familyCode") String familyCode);
}

