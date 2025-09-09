package com.olympus.uga.domain.point.presentation.dto.request;

import com.olympus.uga.domain.point.domain.enums.PaymentType;
import com.olympus.uga.domain.point.domain.enums.PointPackage;

public record PaymentReq(PointPackage pointPackage, PaymentType paymentType) {
}
