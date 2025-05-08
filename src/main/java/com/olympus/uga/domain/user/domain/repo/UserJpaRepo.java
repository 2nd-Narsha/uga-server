package com.olympus.uga.domain.user.domain.repo;

import com.olympus.uga.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepo extends JpaRepository<User, String > {
}
