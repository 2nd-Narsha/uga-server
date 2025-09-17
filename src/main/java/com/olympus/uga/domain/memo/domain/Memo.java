package com.olympus.uga.domain.memo.domain;

import com.olympus.uga.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_memo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Memo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @Column
    private String location;

    @Column
    private String familyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer")
    private User writer;

    @Column
    private LocalDateTime contentUpdatedAt = LocalDateTime.now();

    @Column LocalDateTime locationUpdatedAt = LocalDateTime.now();

    public void updateLocation(String location) {
        this.location = location;
        this.locationUpdatedAt = LocalDateTime.now();
    }

    public void updateContent(String content) {
        this.content = content;
        this.contentUpdatedAt = LocalDateTime.now();
    }
}