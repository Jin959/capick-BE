package com.capick.capick.service;

import com.capick.capick.dto.request.history.storage.StorageOrphanFileHistoriesCreateRequest;
import com.capick.capick.repository.history.storage.StorageOrphanFileHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HistoryService {

    private final StorageOrphanFileHistoryRepository storageOrphanFileHistoryRepository;

    @Transactional
    public void createStorageOrphanFileHistories(
            StorageOrphanFileHistoriesCreateRequest storageOrphanFileHistoriesCreateRequest) {
    }
}
