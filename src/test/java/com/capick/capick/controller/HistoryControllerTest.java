package com.capick.capick.controller;

import com.capick.capick.dto.request.history.storage.StorageOrphanFileHistoriesCreateRequest;
import com.capick.capick.dto.request.history.storage.StorageOrphanFileHistoryCreateRequest;
import com.capick.capick.service.HistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HistoryController.class)
class HistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HistoryService historyService;

    @Test
    @DisplayName("성공: 서비스상 누락 및 삭제되어 DB 가 참조하지 않게 된 외부 저장소의 고아 파일에 대한 기록을 생성한다. HTTP 상태 코드 200 및 자체 응답 코드 204를 반환한다.")
    void createStorageOrphanFileHistories() throws Exception {
        // given
        StorageOrphanFileHistoriesCreateRequest request = StorageOrphanFileHistoriesCreateRequest.builder()
                .orphanFiles(List.of(
                        StorageOrphanFileHistoryCreateRequest.builder()
                                .fileName("000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d")
                                .fileType("images")
                                .domain("reviews")
                                .url("https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2")
                                .build(),
                        StorageOrphanFileHistoryCreateRequest.builder()
                                .fileName("000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d")
                                .fileType("videos")
                                .domain("members")
                                .url("https://storage.com/images2")
                                .build()
                ))
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/history/storage/orphan-files")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("204"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 외부 저장소의 고아 파일에 대한 기록을 생성할 때 파일 정보를 하나라도 입력해야 한다. 하나도 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createStorageOrphanFileHistoriesWithoutAnyFiles() throws Exception {
        // given
        StorageOrphanFileHistoriesCreateRequest request = StorageOrphanFileHistoriesCreateRequest.builder()
                .orphanFiles(List.of())
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/history/storage/orphan-files")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value(
                        "기록을 남길 외부 저장소의 파일 정보를 하나도 입력하지 않았습니다. 파일 정보를 최소 1개는 입력해 주세요."
                ))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 외부 저장소의 고아 파일에 대한 기록을 생성할 때 파일 이름은 필수값이며 공백을 허용하지 않는다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createStorageOrphanFileHistoriesWithoutFileName() throws Exception {
        // given
        StorageOrphanFileHistoriesCreateRequest request = StorageOrphanFileHistoriesCreateRequest.builder()
                .orphanFiles(List.of(
                        StorageOrphanFileHistoryCreateRequest.builder()
                                .fileName("000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d")
                                .fileType("images")
                                .domain("reviews")
                                .url("https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2")
                                .build(),
                        StorageOrphanFileHistoryCreateRequest.builder()
                                .fileName("  ")
                                .fileType("videos")
                                .domain("members")
                                .url("https://storage.com/images2")
                                .build()
                ))
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/history/storage/orphan-files")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("외부 저장소에 저장한 파일의 이름을 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 외부 저장소의 고아 파일에 대한 기록을 생성할 때 파일타입은 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createStorageOrphanFileHistoriesWithoutFileType() throws Exception {
        // given
        StorageOrphanFileHistoriesCreateRequest request = StorageOrphanFileHistoriesCreateRequest.builder()
                .orphanFiles(List.of(
                        StorageOrphanFileHistoryCreateRequest.builder()
                                .fileName("000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d")
                                .fileType("images")
                                .domain("reviews")
                                .url("https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2")
                                .build(),
                        StorageOrphanFileHistoryCreateRequest.builder()
                                .fileName("000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d")
                                .fileType("")
                                .domain("members")
                                .url("https://storage.com/images2")
                                .build()
                ))
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/history/storage/orphan-files")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("외부 저장소에 저장한 파일의 타입을 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 외부 저장소의 고아 파일에 대한 기록을 생성할 때 파일 도메인은 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createStorageOrphanFileHistoriesWithoutFileDomain() throws Exception {
        // given
        StorageOrphanFileHistoriesCreateRequest request = StorageOrphanFileHistoriesCreateRequest.builder()
                .orphanFiles(List.of(
                        StorageOrphanFileHistoryCreateRequest.builder()
                                .fileName("000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d")
                                .fileType("images")
                                .domain("")
                                .url("https://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2")
                                .build(),
                        StorageOrphanFileHistoryCreateRequest.builder()
                                .fileName("000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d")
                                .fileType("videos")
                                .domain("members")
                                .url("https://storage.com/images2")
                                .build()
                ))
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/history/storage/orphan-files")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("외부 저장소에 저장한 파일이 사용되는 도메인을 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 외부 저장소의 고아 파일에 대한 기록을 생성할 때 파일 URL은 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createStorageOrphanFileHistoriesWithoutUrl() throws Exception {
        // given
        StorageOrphanFileHistoriesCreateRequest request = StorageOrphanFileHistoriesCreateRequest.builder()
                .orphanFiles(List.of(
                        StorageOrphanFileHistoryCreateRequest.builder()
                                .fileName("000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d")
                                .fileType("images")
                                .domain("reviews")
                                .build(),
                        StorageOrphanFileHistoryCreateRequest.builder()
                                .fileName("000000001_0_6bc5f946-fcce-4189-a792-ef21d5ae916d")
                                .fileType("videos")
                                .domain("members")
                                .url("https://storage.com/images2")
                                .build()
                ))
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/history/storage/orphan-files")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("외부 저장소에 저장한 파일의 URL 주소를 입력해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    // TODO: Parameterized 가능한가
    @Test
    @DisplayName("예외: 외부 저장소의 고아 파일에 대한 기록을 생성할 때 파일 경로는 URI 형태이고 프로토콜은 HTTP, HTTPS를 사용해야 한다. 그렇지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createStorageOrphanFileHistoriesWithInvalidUrl() throws Exception {
        // given
        StorageOrphanFileHistoriesCreateRequest requestWithProtocolFtp = StorageOrphanFileHistoriesCreateRequest.builder()
                .orphanFiles(List.of(
                        StorageOrphanFileHistoryCreateRequest.builder()
                                .fileName("000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d")
                                .fileType("images")
                                .domain("reviews")
                                .url("ftp://storage.com/images%2Fpathname_encoded%EA%B2%BD%EB%A1%9C/80459?type=image&size=2")
                                .build()
                ))
                .build();
        StorageOrphanFileHistoriesCreateRequest requestWithBlank = StorageOrphanFileHistoriesCreateRequest.builder()
                .orphanFiles(List.of(
                        StorageOrphanFileHistoryCreateRequest.builder()
                                .fileName("000000000_0_6bc5f946-fcce-4189-a792-ef21d5ae916d")
                                .fileType("images")
                                .domain("reviews")
                                .url("storage.com         /images")
                                .build()
                ))
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/history/storage/orphan-files")
                                .content(objectMapper.writeValueAsString(requestWithProtocolFtp))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("허용되지 않는 파일의 URL입니다. URL 형식에 맞추고 프로토콜은 HTTP, HTTPS를 사용해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
        mockMvc.perform(
                        post("/api/history/storage/orphan-files")
                                .content(objectMapper.writeValueAsString(requestWithBlank))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("허용되지 않는 파일의 URL입니다. URL 형식에 맞추고 프로토콜은 HTTP, HTTPS를 사용해 주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

}