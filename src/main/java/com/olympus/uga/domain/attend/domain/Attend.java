package com.olympus.uga.domain.attend.domain;

import com.olympus.uga.domain.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "tb_attend")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attend {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Integer currentStreak; // 현재 연속 출석일수

    @Column(nullable = false)
    private LocalDate lastAttendDate; // 마지막 출석일

    public void updateAttend(LocalDate today, int streak) {
        this.lastAttendDate = today;
        this.currentStreak = streak;
    }

    public void resetStreak() {
        this.currentStreak = 0;
    }
}