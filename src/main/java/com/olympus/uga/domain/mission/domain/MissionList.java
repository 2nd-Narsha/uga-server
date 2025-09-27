package com.olympus.uga.domain.mission.domain;

import com.olympus.uga.domain.mission.domain.enums.MissionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_mission_list")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MissionList {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "mission_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MissionType missionType;

    @Column(name = "reward_points", nullable = false)
    private int rewardPoints;

    @Column(name = "target_count", nullable = false)
    private int targetCount = 1; // 목표 수행 횟수 (기본 1회)

    @Column(name = "action_type", nullable = false)
    private String actionType;  // "LETTER_SEND", "AI_ANSWER", "MEMO_CREATE" 등

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true; // 미션 활성화 여부
}
