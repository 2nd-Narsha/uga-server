package com.olympus.uga.domain.question.domain;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.question.presentation.dto.req.QuestionCreateReq;
import com.olympus.uga.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column
    private String question;

    @Column
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Family family;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private User writer;

    public Question(QuestionCreateReq req, Family family, User user) {
        this.question = req.getContent();
        this.createdAt = LocalDateTime.now();
        this.family = family;
        this.writer = user;
    }
}
