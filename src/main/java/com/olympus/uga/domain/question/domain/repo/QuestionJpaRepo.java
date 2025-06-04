package com.olympus.uga.domain.question.domain.repo;

import com.olympus.uga.domain.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionJpaRepo extends JpaRepository<Question, Long> {
}
