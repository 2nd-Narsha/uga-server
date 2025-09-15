package com.olympus.uga.domain.point.domain;

import com.olympus.uga.domain.point.domain.enums.PointPackage;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_purchase_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Purchase {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "purchase_token_hash", unique = true, nullable = false)
    private String purchaseTokenHash;

    @Column(name = "point_package", nullable = false)
    @Enumerated(EnumType.STRING)
    private PointPackage pointPackage;

    @Column(name = "points_earned", nullable = false)
    private int pointsEarned;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
