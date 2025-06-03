package com.olympus.uga.domain.question.domain;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tb_question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Family family;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;
}
