package com.olympus.uga.domain.album.domain.repo;

import com.olympus.uga.domain.album.domain.Comment;
import com.olympus.uga.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentJpaRepo extends JpaRepository<Comment, Long> {
    Long countByPostPostId(Long postId);
    List<Comment> findByPostPostIdOrderByCreatedAtAsc(Long postId);
    void deleteAllByWriter(User writer);

    @Query("SELECT c FROM Comment c WHERE c.commentId = :commentId AND c.writer.id = :writerId")
    Optional<Comment> findByIdAndWriterId(@Param("commentId") Long commentId, @Param("writerId") Long writerId);
}