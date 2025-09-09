package com.olympus.uga.domain.point.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointPackage {
    PACKAGE_100(100, 1000),
    PACKAGE_550(550, 5000),
    PACKAGE_1200(1200, 10000);

    private final int points;
    private final int price;
}
