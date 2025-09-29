package com.olympus.uga.domain.mission.domain;

import com.olympus.uga.domain.mission.domain.enums.StatusType;
import com.olympus.uga.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_user_mission")
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

    @Column(name = "current_count", nullable = false)
    @Setter
    private int currentCount = 0;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private StatusType status = StatusType.INCOMPLETE;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "rewarded_at")
    private LocalDateTime rewardedAt;

    // 미션 진행도 증가
    public void incrementCount() {
        this.currentCount++;
    }

    // 미션 완료 처리
    public void completeTask() {
        this.status = StatusType.WAITING_REWARD;
        this.completedAt = LocalDateTime.now();
    }

    // 보상 지급 처리
    public void claimReward() {
        this.status = StatusType.COMPLETED;
        this.rewardedAt = LocalDateTime.now();
    }

    // 미션 할당 시 사용하는 정적 팩토리 메서드
    public static UserMission assign(User user, MissionList missionList) {
        return UserMission.builder()
                .user(user)
                .missionList(missionList)
                .currentCount(0)
                .status(StatusType.INCOMPLETE)
                .assignedAt(LocalDateTime.now())
                .build();
    }
}
