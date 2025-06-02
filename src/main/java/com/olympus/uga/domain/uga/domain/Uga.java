package com.olympus.uga.domain.uga.domain;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_uga")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Uga {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private UgaGrowth growth;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime completeGrowthTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "family_code")
    private Family family;
}
