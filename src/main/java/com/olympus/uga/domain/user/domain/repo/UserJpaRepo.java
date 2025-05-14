package com.olympus.uga.domain.user.domain.repo;

import com.olympus.uga.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepo extends JpaRepository<User, Long > {
    Boolean existsByPhoneNum(String phoneNum);
    Optional<User> findByPhoneNum(String email);
    Boolean findByEmail(String phoneNum);
    Optional<User> existsByEmail(String email);
}
