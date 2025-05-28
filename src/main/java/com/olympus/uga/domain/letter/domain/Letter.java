package com.olympus.uga.domain.letter.domain;

import com.olympus.uga.domain.letter.domain.enums.PaperType;
import com.olympus.uga.domain.letter.presentation.dto.req.LetterCreateReq;
import com.olympus.uga.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_letter")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Letter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long letterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User sender;

    @Column
    private String content;

    @Column
    private PaperType paperType;

    @Column
    private LocalDateTime createdAt;

    public Letter(String content, PaperType paperType, User sender) {
        this.content = content;
        this.paperType = paperType;
        this.sender = sender;
        this.createdAt = LocalDateTime.now();
    }
}
