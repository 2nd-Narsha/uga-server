package com.olympus.uga.domain.point.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointPackage {
    UGA_100_COIN(100, 1000),
    UGA_550_COIN(550, 5000),
    UGA_1200_COIN(1200, 10000);

    private final int points;
    private final int price;
}
