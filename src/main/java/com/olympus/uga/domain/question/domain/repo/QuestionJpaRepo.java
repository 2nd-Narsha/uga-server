package com.olympus.uga.domain.question.domain.repo;

import com.olympus.uga.domain.family.domain.Family;
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
public interface QuestionJpaRepo extends JpaRepository<Question, Long> {
    // 특정 가족(family)의 질문 전체를 생성일 기준으로 내림차순 정렬해서 가져오는 메서드
    @Query("SELECT q FROM Question q WHERE q.family = :family ORDER BY q.createdAt DESC")
    List<Question> findByFamilyOrderByCreatedAtDesc(@Param("family") Family family);

    // 특정 가족(family)이 가진 특정 질문(questionId)을 조회하는 메서드
    @Query("SELECT q FROM Question q WHERE q.questionId = :questionId AND q.family = :family")
    Optional<Question> findByQuestionIdAndFamily(@Param("questionId") Long questionId, @Param("family") Family family);

    @Query("SELECT MAX(q.questionId) FROM Question q")
    Optional<Long> findMaxId();

    void deleteAllByWriter(User user);

    @Modifying
    @Query("DELETE FROM Question q WHERE q.family.familyCode = :familyCode")
    void deleteByFamilyCode(@Param("familyCode") String familyCode);
}
