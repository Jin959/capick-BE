package com.capick.capick.domain.cafe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CafeType {

    COFFEE("커피적"),
    SPACIOUS("공간적"),
    COST_EFFECTIVE("가성비"),
    BUSTLING("북적거림");

    private final String text;

}
