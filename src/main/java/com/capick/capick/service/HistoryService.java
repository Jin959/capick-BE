package com.capick.capick.service;

import com.capick.capick.domain.history.storage.StorageOrphanFileHistory;
import com.capick.capick.dto.request.history.storage.StorageOrphanFileHistoriesCreateRequest;
import com.capick.capick.dto.request.history.storage.StorageOrphanFileHistoryCreateRequest;
import com.capick.capick.exception.DuplicateResourceException;
import com.capick.capick.repository.history.storage.StorageOrphanFileHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.capick.capick.dto.ApiResponseStatus.DUPLICATE_ORPHAN_FILE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HistoryService {

    private final StorageOrphanFileHistoryRepository storageOrphanFileHistoryRepository;

    // TODO: 토큰 개발 후 회원만 기록 가능하도록 개발하기
    @Transactional
    public void createStorageOrphanFileHistories(
            StorageOrphanFileHistoriesCreateRequest storageOrphanFileHistoriesCreateRequest) {
        List<StorageOrphanFileHistoryCreateRequest> orphanFiles = storageOrphanFileHistoriesCreateRequest.getOrphanFiles();
        List<String> fileNames = orphanFiles.stream()
                .map(StorageOrphanFileHistoryCreateRequest::getFileName).collect(Collectors.toList());
        List<String> fileTypes = orphanFiles.stream()
                .map(StorageOrphanFileHistoryCreateRequest::getFileType).collect(Collectors.toList());
        List<String> domains = orphanFiles.stream()
                .map(StorageOrphanFileHistoryCreateRequest::getDomain).collect(Collectors.toList());
        List<String> urls = orphanFiles.stream()
                .map(StorageOrphanFileHistoryCreateRequest::getUrl).collect(Collectors.toList());

        ifExistsByUrlsThrow(urls);

        List<StorageOrphanFileHistory> storageOrphanFileHistories
                = StorageOrphanFileHistory.createStorageOrphanFileHistories(fileNames, fileTypes, domains, urls);

        storageOrphanFileHistoryRepository.saveAll(storageOrphanFileHistories);
    }

    private void ifExistsByUrlsThrow(List<String> urls) {
        if (storageOrphanFileHistoryRepository.existsByUrlInAndIsHardDeleted(urls, false)) {
            throw DuplicateResourceException.of(DUPLICATE_ORPHAN_FILE);
        }
    }
}
