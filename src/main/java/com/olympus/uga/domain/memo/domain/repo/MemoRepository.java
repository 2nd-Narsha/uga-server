package com.olympus.uga.domain.memo.domain.repo;

import com.olympus.uga.domain.memo.domain.Memo;
import com.olympus.uga.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
    Optional<Memo> findByWriter(User user);
}
