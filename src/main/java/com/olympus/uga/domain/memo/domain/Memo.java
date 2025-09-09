package com.olympus.uga.domain.memo.domain;

import com.olympus.uga.domain.memo.presentation.dto.req.MemoCreateReq;
import com.olympus.uga.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

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
    private String location = "위치 없음";

    @Column
    private String familyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer")
    private User writer;

    @Column
    private LocalDateTime updatedAt;

    public void updateLocation(String location) {
        this.location = location;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}