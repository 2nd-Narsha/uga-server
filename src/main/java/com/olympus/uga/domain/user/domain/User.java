package com.olympus.uga.domain.user.domain;

import com.olympus.uga.domain.user.domain.enums.Character;
import com.olympus.uga.domain.user.domain.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_user")
public class User {
    @Id
    @Column(name = "phoneNum",nullable = false, unique = true)
    private String phoneNum;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "birth", nullable = false)
    private String birth;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "mbti", nullable = false)
    private String mbti;

    @Column(name = "character", nullable = false)
    private Character character;

    @Column(name = "interests", nullable = false)
    private String interests;
}
