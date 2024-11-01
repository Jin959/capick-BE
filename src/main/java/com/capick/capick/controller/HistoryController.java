package com.capick.capick.controller;

import com.capick.capick.dto.ApiResponse;
import com.capick.capick.dto.request.history.storage.StorageOrphanFileHistoriesCreateRequest;
import com.capick.capick.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.capick.capick.dto.ApiResponseStatus.NO_DATA;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;

    // TODO: 스토리지에서 바로 삭제하는 요청 기능을 개발하고 해당 API SPEC 은 Deprecate시키는 것 고려하기
    @PostMapping("/storage/orphan-files")
    public ApiResponse<Void> createStorageOrphanFileHistories(
            @Valid @RequestBody StorageOrphanFileHistoriesCreateRequest storageOrphanFileHistoriesCreateRequest) {
        historyService.createStorageOrphanFileHistories(storageOrphanFileHistoriesCreateRequest);
        return ApiResponse.of(NO_DATA);
    }

}
