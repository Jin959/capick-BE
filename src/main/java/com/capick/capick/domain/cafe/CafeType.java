package com.capick.capick.domain.cafe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CafeType {

    COFFEE("커피적", "coffeeIndex"),
    SPACIOUS("공간적", "spaceIndex"),
    COST_EFFECTIVE("가성비", "priceIndex"),
    NOISY("시끌벅적함", "noiseIndex"),
    NONE("타입없음", "none");

    private final String text;
    private final String indexName;

    public static CafeType findByIndexName(String indexName) {
        return Arrays.stream(CafeType.values())
                .filter(cafeType -> cafeType.getIndexName().equals(indexName))
                .findFirst()
                .orElseGet(() -> CafeType.NONE);
    }
}
