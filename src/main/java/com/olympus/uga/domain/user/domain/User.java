package com.olympus.uga.domain.user.domain;

import com.olympus.uga.domain.user.domain.enums.UserCharacter;
import com.olympus.uga.domain.user.domain.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "character_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserCharacter character;

    @Column(name = "interests", nullable = false)
    private String interests;
}
