package com.capick.capick.repository;

import com.capick.capick.domain.cafe.Cafe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.capick.capick.domain.common.BaseStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class CafeRepositoryTest {

    @Autowired
    private CafeRepository cafeRepository;

    @Test
    @DisplayName("성공: 외부 지도 API 의 ID 로 까페를 조회할 수 있다.")
    void findByKakaoPlaceIdAndStatus() {
        // given
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url");
        cafeRepository.save(cafe);

        // when
        Optional<Cafe> optionalCafe = cafeRepository.findByKakaoPlaceIdAndStatus(cafe.getKakaoPlaceId(), ACTIVE);

        // then
        assertThat(optionalCafe.isPresent()).isTrue();
        assertThat(optionalCafe.get()).usingRecursiveComparison().isEqualTo(cafe);
    }

    private Cafe createCafe(String name, String kakaoPlaceId, String kakaoDetailPageUrl) {
        return Cafe.builder()
                .name(name)
                .kakaoPlaceId(kakaoPlaceId)
                .kakaoDetailPageUrl(kakaoDetailPageUrl)
                .build();
    }

}