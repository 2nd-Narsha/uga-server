package com.olympus.uga.domain.uga.util;

import com.olympus.uga.domain.uga.domain.enums.FoodType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UgaFeedUtil {
    // 먹이별 성장 촉진 일수
    private static final Map<FoodType, Integer> FOOD_GROWTH_DAYS = Map.of(
            FoodType.BANANA_CHIP, 1,  // 1일
            FoodType.BANANA, 3,       // 3일
            FoodType.BANANA_KICK, 7   // 7일
    );

    // 먹이별 가격
    private static final Map<FoodType, Integer> FOOD_PRICES = Map.of(
            FoodType.BANANA_CHIP, 20,
            FoodType.BANANA, 55,
            FoodType.BANANA_KICK, 120
    );

    public static int getGrowthDays(FoodType foodType) {
        return FOOD_GROWTH_DAYS.getOrDefault(foodType, 0);
    }

    public static int getFoodPrice(FoodType foodType) {
        return FOOD_PRICES.getOrDefault(foodType, 0);
    }

    public static boolean isValidFoodType(FoodType foodType) {
        return FOOD_GROWTH_DAYS.containsKey(foodType);
    }

    public static int calculateTotalCost(FoodType foodType, int familyMemberCount) {
        return getFoodPrice(foodType) * familyMemberCount;
    }
}