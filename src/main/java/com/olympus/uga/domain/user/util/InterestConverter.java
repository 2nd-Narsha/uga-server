package com.olympus.uga.domain.user.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InterestConverter {

    public static List<String> stringToList(String interests) {
        if (interests == null || interests.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(interests.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
