package com.capick.capick.dto.request.history.storage;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class StorageOrphanFileHistoryCreateRequest {

    @NotBlank(message = "외부 저장소에 저장한 파일의 이름을 입력해 주세요.")
    String fileName;

    @NotEmpty(message = "외부 저장소에 저장한 파일의 타입을 입력해 주세요.")
    String fileType;

    @NotEmpty(message = "외부 저장소에 저장한 파일이 사용되는 도메인을 입력해 주세요.")
    String domain;

    @Pattern(
            regexp = "((https?)://)?([\\w가-힇ぁ-ゔァ-ヴー々〆〤一-龥][\\-_./%]?)+(\\?([^#\\s]+)?)?$",
            message = "허용되지 않는 파일의 URL입니다. URL 형식에 맞추고 프로토콜은 HTTP, HTTPS를 사용해 주세요."
    )
    @NotNull(message = "외부 저장소에 저장한 파일의 URL 주소를 입력해 주세요.")
    String url;

    @Builder
    public StorageOrphanFileHistoryCreateRequest(String fileName, String fileType, String domain, String url) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.domain = domain;
        this.url = url;
    }

}
