package com.olympus.uga.domain.calendar.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private String familyCode;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleParticipant> participants;

    public void updateSchedule(String title, LocalDate date,LocalTime startTime, LocalTime endTime) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void addParticipant(Long userId) {
        // participants가 null이면 초기화
        if (this.participants == null) {
            this.participants = new ArrayList<>();
        }
        participants.add(ScheduleParticipant.of(this, userId));
    }

    public void clearParticipants() {
        // participants가 null이면 초기화
        if (this.participants == null) {
            this.participants = new ArrayList<>();
        }
        participants.clear();
    }

    // participants getter에 null 체크 추가
    public List<ScheduleParticipant> getParticipants() {
        if (this.participants == null) {
            this.participants = new ArrayList<>();
        }
        return this.participants;
    }
}
