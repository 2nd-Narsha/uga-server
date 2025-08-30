package com.olympus.uga.domain.user.domain;

import com.olympus.uga.domain.point.error.PointErrorCode;
import com.olympus.uga.domain.user.domain.enums.UserCharacter;
import com.olympus.uga.domain.user.domain.enums.Gender;
import com.olympus.uga.domain.user.domain.enums.LoginType;
import com.olympus.uga.global.exception.CustomException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@SuperBuilder
@Table(name = "tb_user",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"oauth_id", "login_type"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_num", unique = true)
    private String phoneNum;

    @Column(name = "email", unique = false)
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

    // 쉽표로 구분된 문자열로 저장
    @Column(name = "interests", columnDefinition = "TEXT")
    private String interests;

    @Column(name = "login_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(name = "point", nullable = false)
    private int point = 0;

    @Column(name = "profile_image")
    private String profileImage;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tb_watcher")
    private List<Long> watcher = new ArrayList<>();
  
    @Column(name = "tutorial")
    private String tutorial;

    // user setting
    public void updateUsernameBirthGender(String username, String birth, Gender gender) {
        this.username = username;
        this.birth = birth;
        this.gender = gender;
    }
    public void updateInterest(List<String> interestList) {
        if (interestList == null || interestList.isEmpty()) {
            this.interests = "";
            return;
        }
        this.interests = String.join(",", interestList); // 쉼표로 구분해서 저장
    }
    public void updateCharacter(UserCharacter character) {
        this.character = character;
    }
    public void updateMbti(String mbti) {
        this.mbti = mbti;
    }

    // user profile
    public void updateProfile(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateTutorial(String tutorial) { this.tutorial = tutorial; }

    // point
    public void earnPoint(int amount) {
        this.point += amount;
    }
    public void usePoint(int amount) {
        if (this.point < amount) {
            throw new CustomException(PointErrorCode.INSUFFICIENT_POINT);
        }
        this.point -= amount;
    }

    // memo checked member
    public void addWatcher(Long userId) {
        watcher.add(userId);
    }

    public void resetWatcher() {
        watcher.clear();
    }
}

