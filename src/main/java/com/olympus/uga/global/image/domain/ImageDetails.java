package com.olympus.uga.global.image.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tb_image_details")
public class ImageDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private String imagePath;

    @Column(nullable = false)
    private String imageName;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ImageDetails(String imagePath, String imageName) {
        this.imagePath = imagePath;
        this.imageName = imageName;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void update(String imagePath, String imageName) {
        this.imagePath = imagePath;
        this.imageName = imageName;
    }
}