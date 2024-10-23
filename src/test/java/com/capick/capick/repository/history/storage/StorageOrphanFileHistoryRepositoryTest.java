package com.capick.capick.repository.history.storage;

import com.capick.capick.domain.history.storage.FileDomain;
import com.capick.capick.domain.history.storage.FileType;
import com.capick.capick.domain.history.storage.StorageOrphanFileHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.capick.capick.domain.history.storage.FileDomain.MEMBER;
import static com.capick.capick.domain.history.storage.FileDomain.REVIEW;
import static com.capick.capick.domain.history.storage.FileType.IMAGE;
import static com.capick.capick.domain.history.storage.FileType.VIDEO;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class StorageOrphanFileHistoryRepositoryTest {

    @Autowired
    private StorageOrphanFileHistoryRepository storageOrphanFileHistoryRepository;

    @Test
    @DisplayName("성공: 고아 파일의 URL 리스트로 외부 저장소 고아 파일 기록이 하나라도 이미 있는지 조회한다.")
    void existsByUrlInAndIsHardDeleted() {
        // given
        storageOrphanFileHistoryRepository.saveAll(List.of(
                createStorageOrphanFileHistory(
                        "000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", IMAGE, REVIEW,
                        "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2"),
                createStorageOrphanFileHistory(
                        "000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", VIDEO, MEMBER,
                        "https://storage.com/images2"),
                createStorageOrphanFileHistory(
                        "000000002_0_6bc5f946-fcce-4189-a792-ef21d5ae916d", IMAGE, REVIEW,
                        "https://storage.com/images3")
        ));

        // when
        boolean exists = storageOrphanFileHistoryRepository.existsByUrlInAndIsHardDeleted(
                List.of(
                        "https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2",
                        "https://storage.com/dummy2",
                        "https://storage.com/dummy3"
                ),
                false
        );
        boolean notExists = storageOrphanFileHistoryRepository.existsByUrlInAndIsHardDeleted(
                List.of(
                        "https://storage.com/dummy1",
                        "https://storage.com/dummy2",
                        "https://storage.com/dummy3",
                        "https://storage.com/dummy4",
                        "https://storage.com/dummy5",
                        "https://storage.com/dummy6",
                        "https://storage.com/dummy7",
                        "https://storage.com/dummy8",
                        "https://storage.com/dummy9",
                        "https://storage.com/dummy10"
                ),
                false
        );

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
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

}