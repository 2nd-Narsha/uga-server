package com.olympus.uga.domain.uga.domain;

import com.olympus.uga.domain.family.domain.Family;
import com.olympus.uga.domain.uga.domain.enums.UgaGrowth;
import com.olympus.uga.domain.uga.presentation.dto.req.UgaCreateReq;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_uga")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Uga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ugaId;

    @Column
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private UgaGrowth growth;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "family_code")
    private Family family;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime completeGrowthTime;

    public Uga(UgaCreateReq req) {
        this.name = req.getUgaName();
        this.growth = UgaGrowth.BABY;
        this.createdAt = LocalDateTime.now();
        this.completeGrowthTime = LocalDateTime.now().plusDays(365);
    }
}
