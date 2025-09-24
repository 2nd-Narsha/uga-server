package com.olympus.uga.domain.mission.domain;

import com.olympus.uga.domain.mission.domain.enums.StatusType;
import com.olympus.uga.domain.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_user_mission",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "mission_list_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_list_id", nullable = false)
    private MissionList missionList;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusType status = StatusType.INCOMPLETE;

    public void completeMission() {
        this.status = StatusType.WAITING_REWARD;
    }

    public void claimReward() {
        this.status = StatusType.COMPLETED;
    }
}
