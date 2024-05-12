package com.capick.capick.domain.cafe;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CafeThemeTest {

    @Test
    @DisplayName("성공: 전달 받은 까페 테마 이름과 일치하는 까페 테마를 검색한다.")
    void findByThemeName() {
        // given
        String themeName1 = "normal";
        String themeName2 = "vibe";
        String themeName3 = "view";
        String themeName4 = "pet";
        String themeName5 = "hobby";
        String themeName6 = "study";
        String themeName7 = "kids";
        String themeName8 = "etc";

        // when
        CafeTheme cafeTheme1 = CafeTheme.findByThemeName(themeName1);
        CafeTheme cafeTheme2 = CafeTheme.findByThemeName(themeName2);
        CafeTheme cafeTheme3 = CafeTheme.findByThemeName(themeName3);
        CafeTheme cafeTheme4 = CafeTheme.findByThemeName(themeName4);
        CafeTheme cafeTheme5 = CafeTheme.findByThemeName(themeName5);
        CafeTheme cafeTheme6 = CafeTheme.findByThemeName(themeName6);
        CafeTheme cafeTheme7 = CafeTheme.findByThemeName(themeName7);
        CafeTheme cafeTheme8 = CafeTheme.findByThemeName(themeName8);

        // then
        assertThat(cafeTheme1).isEqualByComparingTo(CafeTheme.NORMAL);
        assertThat(cafeTheme2).isEqualByComparingTo(CafeTheme.VIBE);
        assertThat(cafeTheme3).isEqualByComparingTo(CafeTheme.VIEW);
        assertThat(cafeTheme4).isEqualByComparingTo(CafeTheme.PET);
        assertThat(cafeTheme5).isEqualByComparingTo(CafeTheme.HOBBY);
        assertThat(cafeTheme6).isEqualByComparingTo(CafeTheme.STUDY);
        assertThat(cafeTheme7).isEqualByComparingTo(CafeTheme.KIDS);
        assertThat(cafeTheme8).isEqualByComparingTo(CafeTheme.ETC);

    }

    @Test
    @DisplayName("예외: 전달 받은 까페 테마 이름이 존재 하지 않는 경우 ECT 로 반환한다.")
    void findByThemeNameEct() {
        // given
        String indexName = "unexpectedIndex";

        // when
        CafeTheme cafeTheme = CafeTheme.findByThemeName(indexName);

        // then
        assertThat(cafeTheme).isEqualByComparingTo(CafeTheme.ETC);
    }

}