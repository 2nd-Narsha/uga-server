package com.olympus.uga.domain.question.domain.repo;

import com.olympus.uga.domain.question.domain.Answer;
import com.olympus.uga.domain.question.domain.Question;
import com.olympus.uga.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepo extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestion(Question question);
    Optional<Answer> findByQuestionAndWriter(Question question, User user);
}
