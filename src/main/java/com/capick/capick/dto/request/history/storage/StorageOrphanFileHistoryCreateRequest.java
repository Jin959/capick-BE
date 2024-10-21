package com.capick.capick.dto.request.history.storage;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StorageOrphanFileHistoryCreateRequest {

    String fileName;

    String fileType;

    String domain;

    String url;

    @Builder
    public StorageOrphanFileHistoryCreateRequest(String fileName, String fileType, String domain, String url) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.domain = domain;
        this.url = url;
    }

}
