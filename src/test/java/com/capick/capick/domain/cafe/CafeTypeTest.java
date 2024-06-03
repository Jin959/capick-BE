package com.capick.capick.domain.cafe;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CafeTypeTest {

    // TODO: Parameterized 가능한가
    @Test
    @DisplayName("성공: 전달 받은 까페 타입 인덱스 이름과 일치하는 까페 타입을 검색한다.")
    void findByIndexName() {
        // given
        String indexName1 = "coffeeIndex";
        String indexName2 = "spaceIndex";
        String indexName3 = "priceIndex";
        String indexName4 = "noiseIndex";
        String indexName5 = "none";

        // when
        CafeType cafeType1 = CafeType.findByIndexName(indexName1);
        CafeType cafeType2 = CafeType.findByIndexName(indexName2);
        CafeType cafeType3 = CafeType.findByIndexName(indexName3);
        CafeType cafeType4 = CafeType.findByIndexName(indexName4);
        CafeType cafeType5 = CafeType.findByIndexName(indexName5);

        // then
        assertThat(cafeType1).isEqualByComparingTo(CafeType.COFFEE);
        assertThat(cafeType2).isEqualByComparingTo(CafeType.SPACIOUS);
        assertThat(cafeType3).isEqualByComparingTo(CafeType.COST_EFFECTIVE);
        assertThat(cafeType4).isEqualByComparingTo(CafeType.NOISY);
        assertThat(cafeType5).isEqualByComparingTo(CafeType.NONE);
    }

    @Test
    @DisplayName("예외: 전달 받은 까페의 타입 인덱스 이름이 존재하지 않는 경우 NONE 으로 반환한다.")
    void findByIndexNameNone() {
        // given
        String indexName = "unexpectedIndex";

        // when
        CafeType cafeType = CafeType.findByIndexName(indexName);

        // then
        assertThat(cafeType).isEqualByComparingTo(CafeType.NONE);
    }
    
}