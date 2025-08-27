package com.olympus.uga.domain.album.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_album_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Integer imageOrder; // 이미지 순서 (1~5)
}
