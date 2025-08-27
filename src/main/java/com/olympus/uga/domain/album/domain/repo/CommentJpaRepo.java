package com.olympus.uga.domain.album.domain.repo;

import com.olympus.uga.domain.album.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentJpaRepo extends JpaRepository<Comment, Long> {
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.postId = :postId")
    Long countByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.writer WHERE c.commentId = :commentId AND c.writer.id = :writerId")
    Optional<Comment> findByIdAndWriterId(@Param("commentId") Long commentId, @Param("writerId") Long writerId);

    List<Comment> findByPostPostIdOrderByCreatedAtAsc(Long postId);
}