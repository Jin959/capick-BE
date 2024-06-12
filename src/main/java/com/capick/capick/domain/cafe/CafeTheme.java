package com.capick.capick.domain.cafe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CafeTheme {

    NORMAL("일반카페", "normal"),
    VIBE("감성있는", "vibe"),
    VIEW("전경좋은", "view"),
    PET("애완동물까페", "pet"),
    HOBBY("취미활동까페", "hobby"),
    STUDY("스터디카페", "study"),
    KIDS("키즈까페", "kids"),
    ETC("기타테마카페", "etc");

    private final String text;
    private final String name;

    public static CafeTheme findByThemeName(String name) {
        return Arrays.stream(CafeTheme.values())
                .filter(cafeTheme -> cafeTheme.getName().equals(name))
                .findFirst()
                .orElseGet(() -> CafeTheme.ETC);
    }
}
