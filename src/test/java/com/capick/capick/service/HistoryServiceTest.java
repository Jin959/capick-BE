package com.capick.capick.service;

import com.capick.capick.domain.history.storage.StorageOrphanFileHistory;
import com.capick.capick.dto.request.history.storage.StorageOrphanFileHistoriesCreateRequest;
import com.capick.capick.dto.request.history.storage.StorageOrphanFileHistoryCreateRequest;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

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