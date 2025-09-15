package com.olympus.uga.domain.point.presentation.dto.request;

import com.olympus.uga.domain.point.domain.Purchase;
import com.olympus.uga.domain.point.domain.enums.PointPackage;
import com.olympus.uga.domain.user.domain.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

public record PurchaseReq(String purchaseToken, // 인앱결제 완료 후 받은 토큰
                          PointPackage pointPackage) {
    public static Purchase toPurchaseRecord(User user, PurchaseReq req) {
        return Purchase.builder()
                .user(user)
                .purchaseTokenHash(hashToken(req.purchaseToken()))
                .pointPackage(req.pointPackage())
                .pointsEarned(req.pointPackage().getPoints())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public String getHashedToken() {
        return hashToken(this.purchaseToken);
    }

    private static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("토큰 해싱 실패", e);
        }
    }
}
