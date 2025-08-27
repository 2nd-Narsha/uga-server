package com.olympus.uga.domain.album.domain.repo;

import com.olympus.uga.domain.album.domain.Post;
import com.olympus.uga.domain.user.domain.User;
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
    Optional<Post> findByIdAndFamilyCode(@Param("id") Long id, @Param("familyCode") String familyCode);

    // 갤러리용 쿼리 - 이미지가 있는 게시글만 조회
    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.writer " +
            "JOIN p.images i " +
            "WHERE p.family.familyCode = :familyCode " +
            "ORDER BY p.createdAt DESC")
    List<Post> findPostsWithImagesByFamilyCode(@Param("familyCode") String familyCode);

    void deleteAllByWriter(User writer);

    // 회원 탈퇴 시 사용자가 작성한 게시글 조회
    List<Post> findAllByWriter(User writer);
}
