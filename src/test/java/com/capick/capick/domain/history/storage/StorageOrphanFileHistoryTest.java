package com.capick.capick.domain.history.storage;

import com.capick.capick.exception.DomainPoliticalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.capick.capick.domain.history.storage.FileDomain.MEMBER;
import static com.capick.capick.domain.history.storage.FileDomain.REVIEW;
import static com.capick.capick.domain.history.storage.FileType.IMAGE;
import static com.capick.capick.domain.history.storage.FileType.VIDEO;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.tuple;

class StorageOrphanFileHistoryTest {

    @Test
    @DisplayName("성공: 고아 파일 기록 생성 시 물리적 삭제 여부의 초기 값은 false 이다.")
    void createInit() {
        // given
        String fileName = "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d";
        String url = "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2";

        // when
        StorageOrphanFileHistory storageOrphanFileHistory = StorageOrphanFileHistory.create(fileName, IMAGE, REVIEW, url);

        // then
        assertThat(storageOrphanFileHistory.isHardDeleted()).isFalse();
    }

    @Test
    @DisplayName("예외: 고아 파일 기록 리스트 생성 시 중복된 파일이 있을 경우 예외가 발생한다.")
    void createStorageOrphanFileHistoriesWithDuplicateUrls() {
        // given
        List<String> fileNames = List.of(
                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000002_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000003_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "110000004_0_6bc5f946-fcce-4189-a792-ef21d5ae916d"
        );
        List<String> fileTypes = List.of("image", "images", "video", "videos");
        List<String> domains = List.of("member", "members", "review", "reviews");
        List<String> urls = List.of(
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2",
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2",
                "https://storage.com/images3",
                "https://storage.com/images4"
        );

        // when // then
        assertThatThrownBy(() -> StorageOrphanFileHistory.createStorageOrphanFileHistories(
                fileNames, fileTypes, domains, urls))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("요청 파일들끼리 중복되었습니다. 중복된 파일 제외하고 요청해 주세요.");
    }

    @Test
    @DisplayName("예외: 고아 파일 기록 리스트 생성 시 정의한 타입 이외의 파일타입으로 생성할 경우 예외가 발생한다.")
    void createStorageOrphanFileHistoriesWithIllegalFileType() {
        // given
        List<String> fileNames = List.of("000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d");
        List<String> fileTypes = List.of("illegalType");
        List<String> domains = List.of("review");
        List<String> urls = List.of(
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
        );

        // when // then
        assertThatThrownBy(() -> StorageOrphanFileHistory.createStorageOrphanFileHistories(
                fileNames, fileTypes, domains, urls))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("파일 기록 시 허락되지 않은 파일타입입니다.");
    }

    @Test
    @DisplayName("예외: 고아 파일 기록 리스트 생성 시 정의한 도메인 이외의 도메인으로 생성할 경우 예외가 발생한다.")
    void createStorageOrphanFileHistoriesWithIllegalDomain() {
        // given
        List<String> fileNames = List.of("000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d");
        List<String> fileTypes = List.of("image");
        List<String> domains = List.of("illegalDomain");
        List<String> urls = List.of(
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
        );

        // when // then
        assertThatThrownBy(() -> StorageOrphanFileHistory.createStorageOrphanFileHistories(
                fileNames, fileTypes, domains, urls))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("파일 기록 시 허락되지 않은 도메인입니다.");
    }

    @Test
    @DisplayName("예외: 고아 파일 기록 리스트 생성 시 최대 10개 까지만 가능하다. 넘어가면 예외가 발생한다.")
    void createStorageOrphanFileHistoriesWithFileNumberExceeded() {
        // given
        List<String> fileNames = List.of(
                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000002_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000003_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000004_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000005_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000006_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000007_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000008_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000009_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000010_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000011_0_6bc5f946-fcce-4189-a792-ef21d5ae916d"
        );
        List<String> fileTypes = List.of(
                "image", "images", "video", "videos", "images",
                "video", "videos", "images", "video", "videos", "videos"
        );
        List<String> domains = List.of(
                "member", "members", "review", "reviews", "members",
                "review", "reviews", "members", "review", "reviews", "reviews"
        );
        List<String> urls = List.of(
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2",
                "https://storage.com/images2",
                "https://storage.com/images3",
                "https://storage.com/images4",
                "https://storage.com/images5",
                "https://storage.com/images6",
                "https://storage.com/images7",
                "https://storage.com/images8",
                "https://storage.com/images9",
                "https://storage.com/images10",
                "https://storage.com/images11"
        );

        // when // then
        assertThatThrownBy(() -> StorageOrphanFileHistory.createStorageOrphanFileHistories(
                fileNames, fileTypes, domains, urls))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("고아 파일 기록 시 한꺼번에 10개까지만 할 수 있습니다.");
    }

    @Test
    @DisplayName("경계: 고아 파일 리스트 생성 시 한꺼번에 10개까지는 등록할 수 있다.")
    void createStorageOrphanFileHistoriesWithMaxNumberOfFiles() {
        // given
        List<String> fileNames = List.of(
                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000002_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000003_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000004_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000005_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000006_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000007_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000008_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000009_0_6bc5f946-fcce-4189-a792-ef21d5ae916d",
                "000000010_0_6bc5f946-fcce-4189-a792-ef21d5ae916d"
        );
        List<String> fileTypes = List.of(
                "video", "videos", "images", "images", "images",
                "images", "images", "images", "images", "images"
        );
        List<String> domains = List.of(
                "member", "members", "review", "reviews", "review",
                "review", "reviews", "review", "review", "reviews"
        );
        List<String> urls = List.of(
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2",
                "https://storage.com/images2",
                "https://storage.com/images3",
                "https://storage.com/images4",
                "https://storage.com/images5",
                "https://storage.com/images6",
                "https://storage.com/images7",
                "https://storage.com/images8",
                "https://storage.com/images9",
                "https://storage.com/images10"
        );

        // when
        List<StorageOrphanFileHistory> storageOrphanFileHistories
                = StorageOrphanFileHistory.createStorageOrphanFileHistories(fileNames, fileTypes, domains, urls);

        // then
        assertThat(storageOrphanFileHistories)
                .hasSize(10)
                .extracting("fileName", "fileType", "domain", "url")
                .containsExactlyInAnyOrder(
                        tuple(
                                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", VIDEO, MEMBER,
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                        ),
                        tuple(
                                "000000002_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", VIDEO, MEMBER,
                                "https://storage.com/images2"
                        ),
                        tuple(
                                "000000003_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", IMAGE, REVIEW,
                                "https://storage.com/images3"
                        ),
                        tuple(
                                "000000004_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", IMAGE, REVIEW,
                                "https://storage.com/images4"
                        ),
                        tuple(
                                "000000005_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", IMAGE, REVIEW,
                                "https://storage.com/images5"
                        ),
                        tuple(
                                "000000006_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", IMAGE, REVIEW,
                                "https://storage.com/images6"
                        ),
                        tuple(
                                "000000007_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", IMAGE, REVIEW,
                                "https://storage.com/images7"
                        ),
                        tuple(
                                "000000008_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", IMAGE, REVIEW,
                                "https://storage.com/images8"
                        ),
                        tuple(
                                "000000009_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", IMAGE, REVIEW,
                                "https://storage.com/images9"
                        ),
                        tuple(
                                "000000010_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", IMAGE, REVIEW,
                                "https://storage.com/images10"
                        )
                );
    }

}