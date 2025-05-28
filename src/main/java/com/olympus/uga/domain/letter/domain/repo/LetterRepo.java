package com.olympus.uga.domain.letter.domain.repo;

import com.olympus.uga.domain.letter.domain.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterRepo extends JpaRepository<Letter, Long> {
}

