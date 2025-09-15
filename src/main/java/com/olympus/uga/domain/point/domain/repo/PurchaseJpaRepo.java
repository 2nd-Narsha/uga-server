package com.olympus.uga.domain.point.domain.repo;

import com.olympus.uga.domain.point.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseJpaRepo extends JpaRepository<Purchase, Long> {
    boolean existsByPurchaseTokenHash(String purchaseTokenHash);
}
