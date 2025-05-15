package com.olympus.uga.domain.user.domain.repo;

import com.olympus.uga.domain.user.domain.User;
import com.olympus.uga.domain.user.domain.enums.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepo extends JpaRepository<User, Long > {
    Boolean existsByPhoneNum(String phoneNum);
    Optional<User> findByPhoneNum(String email);
    Optional<User> findByOauthIdAndLoginType(String oauthId, LoginType loginType);
}
