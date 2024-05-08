package com.capick.capick.domain.cafe;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CafeThemeInfo {

    @Column(nullable = false)
    private int normalCount;

    @Column(nullable = false)
    private int vibeCount;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int petCount;

    @Column(nullable = false)
    private int hobbyCount;

    @Column(nullable = false)
    private int studyCount;

    @Column(nullable = false)
    private int kidsCount;

    @Column(nullable = false)
    private int etcCount;

    @Enumerated(EnumType.STRING)
    private CafeTheme cafeTheme;

}
