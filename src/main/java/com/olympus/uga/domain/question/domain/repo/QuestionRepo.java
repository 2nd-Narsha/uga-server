package com.olympus.uga.domain.question.domain.repo;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepo extends JpaRepository<Question, Long> {
    List<Question> findByFamily(Family family);
}
