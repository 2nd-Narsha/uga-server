package com.olympus.uga.domain.calendar.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_dday")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_code", nullable = false)
    private String familyCode;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "start_time")
    private String startTime; // "14:30" 형식

    @Column(name = "end_time")
    private String endTime; // "16:00" 형식

    @Builder.Default
    @Column(name = "is_highlight")
    private Boolean isHighlight = false;

    @Builder.Default
    @Column(name = "is_notification_sent")
    private Boolean isNotificationSent = false;

    public void markNotificationSent() {
        this.isNotificationSent = true;
    }

    public void updateDday(String title, LocalDate date, String startTime, String endTime, Boolean isHighlight) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isHighlight = isHighlight;
    }
}
