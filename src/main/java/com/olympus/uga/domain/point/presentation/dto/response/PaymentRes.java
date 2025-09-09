package com.olympus.uga.domain.point.presentation.dto.response;

public record PaymentRes(String paymentId,
                         String paymentUrl,
                         int points,
                         int price,
                         String status) {
}
