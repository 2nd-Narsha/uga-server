package com.olympus.uga.domain.question.domain;

import com.olympus.uga.domain.question.presentation.dto.req.AnswerReq;
import com.olympus.uga.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_answer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Column
    private String answer;

    @Column
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn
    private User writer;

    @ManyToOne
    @JoinColumn
    private Question question;

    public Answer(String answer, User writer, Question question) {
        this.answer = answer;
        this.writer = writer;
        this.question = question;
        this.createdAt = LocalDateTime.now();
    }
}
