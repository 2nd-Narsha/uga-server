package com.olympus.uga.domain.letter.domain.repo;

import com.olympus.uga.domain.letter.domain.Letter;
import com.olympus.uga.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LetterJpaRepo extends JpaRepository<Letter, Long> {
    // 특정 사용자가 받은 편지 목록 조회
    @Query("SELECT l FROM Letter l WHERE l.receiver = :receiver ORDER BY l.sentAt DESC")
    List<Letter> findByReceiver(@Param("receiver") User receiver);

    // 특정 사용자가 받은 편지 중 특정 ID의 편지 조회
    @Query("SELECT l FROM Letter l WHERE l.letterId = :letterId AND l.receiver = :receiver")
    Optional<Letter> findByIdAndReceiver(@Param("letterId") Long letterId, @Param("receiver") User receiver);

    void deleteAllBySenderOrReceiver(User sender, User receiver);
}
