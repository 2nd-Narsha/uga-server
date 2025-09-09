package com.olympus.uga.domain.answer.domain.repo;

import com.olympus.uga.domain.answer.domain.Answer;
import com.olympus.uga.domain.question.domain.Question;
import com.olympus.uga.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerJpaRepo extends JpaRepository<Answer, Long> {
    // 특정 질문에 대한 모든 답변 조회
    @Query("SELECT a FROM Answer a WHERE a.question = :question")
    List<Answer> findByQuestion(@Param("question") Question question);

    // 특정 사용자가 특정 질문에 답변했는지 확인
    @Query("SELECT a FROM Answer a WHERE a.question = :question AND a.writer = :writer")
    Optional<Answer> findByQuestionAndWriter(@Param("question") Question question, @Param("writer") User writer);

    // 유저가 답한 질문 ID 목록 조회
    @Query("SELECT a.question.questionId FROM Answer a WHERE a.writer = :writer")
    List<Long> findAnsweredQuestionIdsByWriter(@Param("writer") User writer);

    void deleteAllByWriter(User user);

    @Modifying
    @Query("DELETE FROM Answer a WHERE a.question.family.familyCode = :familyCode")
    void deleteByFamilyCode(@Param("familyCode") String familyCode);
}
