package com.olympus.uga.domain.calendar.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_schedule_participant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleParticipant { // 일정 참여 인원
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public static ScheduleParticipant of(Schedule schedule, Long userId) {
        return ScheduleParticipant.builder()
                .schedule(schedule)
                .userId(userId)
                .build();
    }
}