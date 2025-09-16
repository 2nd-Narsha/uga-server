package com.olympus.uga.domain.user.domain.repo;

import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.enums.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserJpaRepo extends JpaRepository<User, Long> {
    Boolean existsByPhoneNum(String phoneNum);
    Optional<User> findByPhoneNum(String email);
    Optional<User> findByOauthIdAndLoginType(String oauthId, LoginType loginType);
    List<User> findAllByFamilyCode(String familyCode);
    
    /**
     * 가족 코드로 사용자 목록 조회 (별칭 메소드)
     */
    List<User> findByFamilyCode(String familyCode);

    /**
     * 지정된 기간 동안 비활성 사용자 조회
     */
    @Query("SELECT u FROM User u WHERE u.lastActivityAt < :threshold AND u.fcmToken IS NOT NULL")
    List<User> findUsersInactiveForDays(@Param("threshold") LocalDateTime threshold);
    
    /**
     * 가족별 FCM 토큰이 있는 사용자 조회
     */
    @Query("SELECT u FROM User u WHERE u.familyCode = :familyCode AND u.fcmToken IS NOT NULL")
    List<User> findByFamilyCodeWithFcmToken(@Param("familyCode") String familyCode);
}
