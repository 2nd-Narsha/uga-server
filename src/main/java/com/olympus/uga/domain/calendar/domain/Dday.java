package com.olympus.uga.domain.calendar.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_dday")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dday {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Boolean isHighlight; // 팡파레

    @Column(nullable = false)
    private String familyCode;

    public void updateDday(String title, LocalDate date, Boolean isHighlight) {
        this.title = title;
        this.date = date;
        this.isHighlight = isHighlight;
    }
}
