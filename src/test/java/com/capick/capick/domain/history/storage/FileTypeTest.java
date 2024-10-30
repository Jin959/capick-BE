package com.capick.capick.domain.history.storage;

import com.capick.capick.exception.DomainPoliticalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileTypeTest {

    // TODO: Parameterized 가능한가
    @Test
    @DisplayName("성공: 단수형 또는 복수형으로 표현한 타입과 일치하는 파일 타입을 검색한다.")
    void findByTypeOrTypeInPlural() {
        // given
        String type1 = "image";
        String type2 = "images";
        String type3 = "video";
        String type4 = "videos";

        // when
        FileType fileType1 = FileType.findByTypeOrTypeInPlural(type1);
        FileType fileType2 = FileType.findByTypeOrTypeInPlural(type2);
        FileType fileType3 = FileType.findByTypeOrTypeInPlural(type3);
        FileType fileType4 = FileType.findByTypeOrTypeInPlural(type4);

        // then
        assertThat(fileType1).isEqualByComparingTo(FileType.IMAGE);
        assertThat(fileType2).isEqualByComparingTo(FileType.IMAGE);
        assertThat(fileType3).isEqualByComparingTo(FileType.VIDEO);
        assertThat(fileType4).isEqualByComparingTo(FileType.VIDEO);
    }

    @Test
    @DisplayName("예외: 파일 타입 검색 결과가 존재하지 않는 경우 예외가 발생한다.")
    void findByTypeOrTypeInPluralButNoResults() {
        // given
        String type = "illegalType";
        
        // when // then
        assertThatThrownBy(() -> FileType.findByTypeOrTypeInPlural(type))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("파일 기록 시 허락되지 않은 파일타입입니다.");
    }

}