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
    @Query("SELECT p FROM Post p JOIN FETCH p.writer WHERE p.family.familyCode = :familyCode ORDER BY p.createdAt DESC")
    List<Post> findByFamilyCodeOrderByCreatedAtDesc(@Param("familyCode") String familyCode);

    @Query("SELECT p FROM Post p JOIN FETCH p.writer WHERE p.postId = :id AND p.family.familyCode = :familyCode")
    Optional<Post> findByIdAndFamilyCode(Long id, String familyCode);

    // 갤러리용 쿼리 - 이미지가 있는 게시글만 조회
    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.writer " +
            "WHERE p.family.familyCode = :familyCode AND " +
            "EXISTS (SELECT 1 FROM PostImage pi WHERE pi.post = p) " +
            "ORDER BY p.createdAt DESC")
    List<Post> findPostsWithImagesByFamilyCode(@Param("familyCode") String familyCode);

    @Query("SELECT p FROM Post p JOIN FETCH p.writer WHERE p.postId = :postId AND p.writer.id = :writerId")
    Optional<Post> findByIdAndWriterId(@Param("postId") Long postId, @Param("writerId") Long writerId);
}
