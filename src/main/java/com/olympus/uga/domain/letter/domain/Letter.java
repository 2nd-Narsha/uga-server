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

import java.time.LocalDate;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_letter")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Letter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long letterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // 보내는 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // 받는 사람

    @Column
    private PaperType paperType;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDate sentAt; // 보낸 날짜

    @Column(nullable = false)
    private Boolean isRead = false;

    // 편지 읽음 처리 메서드
    public void markAsRead() {
        this.isRead = true;
    }
}
