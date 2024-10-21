package com.capick.capick.dto.request.history.storage;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StorageOrphanFileHistoriesCreateRequest {

    List<StorageOrphanFileHistoryCreateRequest> orphanFiles;

    @Builder
    public StorageOrphanFileHistoriesCreateRequest(List<StorageOrphanFileHistoryCreateRequest> orphanFiles) {
        this.orphanFiles = orphanFiles;
    }

}
