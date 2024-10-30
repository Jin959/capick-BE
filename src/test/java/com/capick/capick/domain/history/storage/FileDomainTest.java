package com.capick.capick.domain.history.storage;

import com.capick.capick.exception.DomainPoliticalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.capick.capick.domain.history.storage.FileDomain.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileDomainTest {

    // TODO: Parameterized 가능한가
    @Test
    @DisplayName("성공: 단수형 또는 복수형으로 표현한 도메인과 일치하는 파일 도메인을 검색한다.")
    void findByDomainOrDomainInPlural() {
        // given
        String domain1 = "member";
        String domain2 = "members";
        String domain3 = "review";
        String domain4 = "reviews";
        String domain5 = "etc";
        String domain6 = "etcs";

        // when
        FileDomain fileDomain1 = FileDomain.findByDomainOrDomainInPlural(domain1);
        FileDomain fileDomain2 = FileDomain.findByDomainOrDomainInPlural(domain2);
        FileDomain fileDomain3 = FileDomain.findByDomainOrDomainInPlural(domain3);
        FileDomain fileDomain4 = FileDomain.findByDomainOrDomainInPlural(domain4);
        FileDomain fileDomain5 = FileDomain.findByDomainOrDomainInPlural(domain5);
        FileDomain fileDomain6 = FileDomain.findByDomainOrDomainInPlural(domain6);

        // then
        assertThat(fileDomain1).isEqualByComparingTo(MEMBER);
        assertThat(fileDomain2).isEqualByComparingTo(MEMBER);
        assertThat(fileDomain3).isEqualByComparingTo(REVIEW);
        assertThat(fileDomain4).isEqualByComparingTo(REVIEW);
        assertThat(fileDomain5).isEqualByComparingTo(ETC);
        assertThat(fileDomain6).isEqualByComparingTo(ETC);
    }

    @Test
    @DisplayName("예외: 파일 도메인 검색 결과가 존재하지 않는 경우 예외가 발생한다.")
    void findByDomainOrDomainInPluralButNoResults() {
        // given
        String domain = "illegalDomain";

        // when // then
        assertThatThrownBy(() -> FileDomain.findByDomainOrDomainInPlural(domain))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("파일 기록 시 허락되지 않은 도메인입니다.");
    }

}