package com.olympus.uga.domain.letter.domain;

import com.olympus.uga.domain.letter.domain.enums.PaperType;
import com.olympus.uga.domain.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_letter")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Letter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender; // 보내는 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver; // 받는 사람

    @Column
    private PaperType paperType;

    @Column(nullable = false)
    private String content;

    @Column()
    private LocalDateTime sentAt; // 보낸 날짜
}
