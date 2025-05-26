package com.olympus.uga.domain.user.domain;

import com.olympus.uga.domain.uga.domain.enums.FoodType;
import com.olympus.uga.domain.user.domain.enums.UserCharacter;
import com.olympus.uga.domain.user.domain.enums.Gender;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

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

    @Column(name = "username")
    private String username;

    @Column(name = "birth")
    private String birth;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "mbti")
    private String mbti;

    @Column(name = "point")
    private int point;

    @Column(name = "used_point")
    private int usedPoint;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<FoodType> foods;

    @Column(name = "character_type")
    @Enumerated(EnumType.STRING)
    private UserCharacter character;

    @Column(name = "interests")
    private String interests;
}
