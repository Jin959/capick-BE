package com.capick.capick.service;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.common.Location;
import com.capick.capick.dto.response.CafeResponse;
import com.capick.capick.exception.NotFoundResourceException;
import com.capick.capick.repository.CafeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.capick.capick.domain.cafe.CafeTheme.NORMAL;
import static com.capick.capick.domain.cafe.CafeType.NONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
class CafeServiceTest {

    @Autowired
    private CafeService cafeService;

    @Autowired
    private CafeRepository cafeRepository;

    @AfterEach
    void tearDown() {
        cafeRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("성공: 회원 또는 방문자는 카카오 지도와 같은 외부 지도 서비스 업체에서 제공한 ID로 카페 정보를 조회할 수 있다.")
    void getCafeByMapVendorPlaceId() {
        // given
        Location cafeLocation = createLocation(
                37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
        Cafe cafe = createCafe("스타벅스 광화문점", "1234567", "https://place.url/1234567", cafeLocation);
        String kakaoPlaceId = cafeRepository.save(cafe).getKakaoPlaceId();

        // when
        CafeResponse response = cafeService.getCafeByMapVendorPlaceId(kakaoPlaceId);

        // then
        assertThat(response)
                .extracting("name", "kakaoPlaceId", "kakaoDetailPageUrl", "cafeType", "cafeTheme")
                .contains("스타벅스 광화문점", "1234567", "https://place.url/1234567", NONE, NORMAL);
        assertThat(response.getLocation())
                .extracting("latitude", "longitude", "address", "roadAddress")
                .contains(37.57122962143047, 126.97629649901215, "서울 종로구 세종로 00-0", "서울 종로구 세종대로 000");
    }

    @Test
    @DisplayName("예외: 지도 서비스상의 ID로 카페 조회 시 등록된 적이 없거나 삭제되어 존재하지 않는 카페이면 예외가 발생한다.")
    void getNotExistCafeByMapVendorPlaceId() {
        // given
        String notCreatedCafeKakaoPlaceId = "1234567";

        // when // then
        assertThatThrownBy(() -> cafeService.getCafeByMapVendorPlaceId(notCreatedCafeKakaoPlaceId))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessage("등록된 적이 없거나 삭제되어 서비스상에서 존재하지 않는 카페입니다.");
    }

    private Location createLocation(double latitude, double longitude, String address, String roadAddress) {
        return Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .address(address)
                .roadAddress(roadAddress)
                .build();
    }

    private Cafe createCafe(String name, String kakaoPlaceId, String kakaoDetailPageUrl, Location location) {
        return Cafe.builder()
                .name(name)
                .kakaoPlaceId(kakaoPlaceId)
                .kakaoDetailPageUrl(kakaoDetailPageUrl)
                .location(location)
                .build();
    }

}