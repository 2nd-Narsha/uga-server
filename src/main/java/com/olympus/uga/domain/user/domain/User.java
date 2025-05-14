package com.olympus.uga.domain.user.domain;

import com.olympus.uga.domain.user.domain.enums.LoginType;
import com.olympus.uga.domain.user.domain.enums.UserCharacter;
import com.olympus.uga.domain.user.domain.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_num", unique = true)
    private String phoneNum;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "birth")
    private String birth;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "mbti")
    private String mbti;

    @Column(name = "character_type")
    @Enumerated(EnumType.STRING)
    private UserCharacter character;

    @Column(name = "interests")
    private String interests;

    @Column(name = "login_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;
}
