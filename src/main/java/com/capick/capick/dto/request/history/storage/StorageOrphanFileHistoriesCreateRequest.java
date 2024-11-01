package com.capick.capick.dto.request.history.storage;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@NoArgsConstructor
public class StorageOrphanFileHistoriesCreateRequest {

    @NotEmpty(message = "기록을 남길 외부 저장소의 파일 정보를 하나도 입력하지 않았습니다. 파일 정보를 최소 1개는 입력해 주세요.")
    @Valid
    List<StorageOrphanFileHistoryCreateRequest> orphanFiles;

    @Builder
    public StorageOrphanFileHistoriesCreateRequest(List<StorageOrphanFileHistoryCreateRequest> orphanFiles) {
        this.orphanFiles = orphanFiles;
    }

}
