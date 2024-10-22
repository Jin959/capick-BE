package com.capick.capick.service;

import com.capick.capick.domain.history.storage.FileDomain;
import com.capick.capick.domain.history.storage.FileType;
import com.capick.capick.domain.history.storage.StorageOrphanFileHistory;
import com.capick.capick.dto.request.history.storage.StorageOrphanFileHistoriesCreateRequest;
import com.capick.capick.dto.request.history.storage.StorageOrphanFileHistoryCreateRequest;
import com.capick.capick.exception.DomainPoliticalArgumentException;
import com.capick.capick.exception.DuplicateResourceException;
import com.capick.capick.repository.history.storage.StorageOrphanFileHistoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.capick.capick.domain.history.storage.FileDomain.MEMBER;
import static com.capick.capick.domain.history.storage.FileDomain.REVIEW;
import static com.capick.capick.domain.history.storage.FileType.IMAGE;
import static com.capick.capick.domain.history.storage.FileType.VIDEO;
import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class HistoryServiceTest {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private StorageOrphanFileHistoryRepository storageOrphanFileHistoryRepository;

    @AfterEach
    void tearDown() {
        storageOrphanFileHistoryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("성공: 외부 저장소에 파일 업로드는 되었지만, 리뷰 생성 및 프로필 등록 등 후속 처리 시 예외로 인해 DB 가 참조하지 않게 된 고아 파일을 기록할 수 있다.")
    void createStorageOrphanFileHistories() {
        // given
        StorageOrphanFileHistoriesCreateRequest request = createStorageOrphanFileHistoriesCreateRequest(
                List.of(
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "images", "reviews",
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images2"
                        )
                )
        );

        // when
        historyService.createStorageOrphanFileHistories(request);

        // then
        List<StorageOrphanFileHistory> storageOrphanFileHistories = storageOrphanFileHistoryRepository.findAll();
        assertThat(storageOrphanFileHistories).hasSize(2)
                .extracting("fileName", "fileType", "domain", "url")
                .containsExactlyInAnyOrder(
                        tuple(
                                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", IMAGE, REVIEW,
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                        ),
                        tuple(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", VIDEO, MEMBER,
                                "https://storage.com/images2"
                        )
                );
    }

    @Test
    @DisplayName("성공: 고아 파일 기록 시 중복 된 파일이 기록될 경우 중복은 제거하고 저장한다.")
    void createStorageOrphanFileHistoriesWithDuplicateUrls() {
        // given
        StorageOrphanFileHistoriesCreateRequest request = createStorageOrphanFileHistoriesCreateRequest(
                List.of(
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "images", "reviews",
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "images", "reviews",
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                        )
                )
        );

        // when
        historyService.createStorageOrphanFileHistories(request);

        // then
        List<StorageOrphanFileHistory> storageOrphanFileHistories = storageOrphanFileHistoryRepository.findAll();
        assertThat(storageOrphanFileHistories).hasSize(1)
                .extracting("fileName", "fileType", "domain", "url")
                .contains(
                        tuple(
                                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", IMAGE, REVIEW,
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                        )
                );
    }

    @Test
    @DisplayName("예외: 고아 파일 기록 시 이미 기록했던 파일을 기록하려 하는 경우 예외가 발생한다.")
    void createStorageOrphanFileHistoriesWithDuplicateOrphanFileHistory() {
        // given
        StorageOrphanFileHistory storageOrphanFileHistory = createStorageOrphanFileHistory(
                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", IMAGE, REVIEW,
                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2");
        storageOrphanFileHistoryRepository.save(storageOrphanFileHistory);

        StorageOrphanFileHistoriesCreateRequest request = createStorageOrphanFileHistoriesCreateRequest(
                List.of(
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "images", "reviews",
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                        )
                )
        );

        // when // then
        assertThatThrownBy(() -> historyService.createStorageOrphanFileHistories(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("업로드 된 고아 파일 기록이 이미 있습니다.");
    }

    @Test
    @DisplayName("예외: 고아 파일 기록 시 이미지, 비디오 등 정해진 타입 이외의 타입을 업로드 및 기록할 경우 예외가 발생한다.")
    void createStorageOrphanFileHistoriesWithIllegalFileType() {
        // given
        StorageOrphanFileHistoriesCreateRequest request = createStorageOrphanFileHistoriesCreateRequest(
                List.of(
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "illegalType", "reviews",
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                        )
                )
        );

        // when // then
        assertThatThrownBy(() -> historyService.createStorageOrphanFileHistories(request))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("고아 파일 기록 시 허락되지 않은 타입의 파일입니다.");
    }

    @Test
    @DisplayName("예외: 고아 파일 기록 시 허락되지 않은 도메인을 사용하여 기록할 경우 예외가 발생한다.")
    void createStorageOrphanFileHistoriesWithIllegalDomain() {
        // given
        StorageOrphanFileHistoriesCreateRequest request = createStorageOrphanFileHistoriesCreateRequest(
                List.of(
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "image", "illegalDomain",
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                        )
                )
        );

        // when // then
        assertThatThrownBy(() -> historyService.createStorageOrphanFileHistories(request))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("고아 파일 기록 시 허락되지 않은 도메인입니다.");
    }

    @Test
    @DisplayName("예외: 고아 파일 기록 시 파일을 한꺼번에 10개보다 많이 등록할 경우 도메인 정책상 예외가 발생한다.")
    void createOrphanFileHistoriesWithFileNumberExceeded() {
        // given
        StorageOrphanFileHistoriesCreateRequest request = createStorageOrphanFileHistoriesCreateRequest(
                List.of(
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "images", "reviews",
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images2"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images3"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images4"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images5"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images6"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images7"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images8"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images9"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images10"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images11"
                        )
                )
        );

        // when // then
        assertThatThrownBy(() -> historyService.createStorageOrphanFileHistories(request))
                .isInstanceOf(DomainPoliticalArgumentException.class)
                .hasMessage("고아 파일 기록 시 한꺼번에 10개까지만 할 수 있습니다.");
    }

    @Test
    @DisplayName("경계: 고아 파일 기록 시 파일을 한꺼번에 10개까지는 등록할 수 있다.")
    void createOrphanFileHistoriesWithMaxNumberOfFiles() {
        // given
        StorageOrphanFileHistoriesCreateRequest request = createStorageOrphanFileHistoriesCreateRequest(
                List.of(
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "images", "reviews",
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images2"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images3"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images4"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images5"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images6"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images7"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images8"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images9"
                        ),
                        createStorageOrphanFileHistoryCreateRequest(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", "videos", "members",
                                "https://storage.com/images10"
                        )
                )
        );

        // when
        historyService.createStorageOrphanFileHistories(request);

        // then
        List<StorageOrphanFileHistory> storageOrphanFileHistories = storageOrphanFileHistoryRepository.findAll();
        assertThat(storageOrphanFileHistories)
                .hasSize(10)
                .extracting("fileName", "fileType", "domain", "url")
                .containsExactlyInAnyOrder(
                        tuple(
                                "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", IMAGE, REVIEW,
                                "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"
                        ),
                        tuple(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", VIDEO, MEMBER,
                                "https://storage.com/images2"
                        ),
                        tuple(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", VIDEO, MEMBER,
                                "https://storage.com/images3"
                        ),
                        tuple(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", VIDEO, MEMBER,
                                "https://storage.com/images4"
                        ),
                        tuple(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", VIDEO, MEMBER,
                                "https://storage.com/images5"
                        ),
                        tuple(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", VIDEO, MEMBER,
                                "https://storage.com/images6"
                        ),
                        tuple(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", VIDEO, MEMBER,
                                "https://storage.com/images7"
                        ),
                        tuple(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", VIDEO, MEMBER,
                                "https://storage.com/images8"
                        ),
                        tuple(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", VIDEO, MEMBER,
                                "https://storage.com/images9"
                        ),
                        tuple(
                                "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", VIDEO, MEMBER,
                                "https://storage.com/images10"
                        )
                );
    }

    private StorageOrphanFileHistory createStorageOrphanFileHistory(
            String fileName, FileType fileType, FileDomain domain, String url) {
        return StorageOrphanFileHistory.builder()
                .fileName(fileName)
                .fileType(fileType)
                .domain(domain)
                .url(url)
                .build();
    }

    private StorageOrphanFileHistoriesCreateRequest createStorageOrphanFileHistoriesCreateRequest(
            List<StorageOrphanFileHistoryCreateRequest> orphanFiles) {
        return StorageOrphanFileHistoriesCreateRequest.builder()
                .orphanFiles(orphanFiles)
                .build();
    }

    private StorageOrphanFileHistoryCreateRequest createStorageOrphanFileHistoryCreateRequest(
            String fileName, String fileType, String domain, String url) {
        return StorageOrphanFileHistoryCreateRequest.builder()
                .fileName(fileName)
                .fileType(fileType)
                .domain(domain)
                .url(url)
                .build();
    }

}