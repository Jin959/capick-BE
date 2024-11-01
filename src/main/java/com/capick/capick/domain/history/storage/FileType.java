package com.capick.capick.domain.history.storage;

import com.capick.capick.exception.DomainPoliticalArgumentException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.capick.capick.dto.ApiResponseStatus.ILLEGAL_FILE_TYPE_ERROR;

@Getter
@RequiredArgsConstructor
public enum FileType {

    IMAGE("이미지 파일", "image", "images"),
    VIDEO("동영상 파일", "video", "videos");

    private final String text;
    private final String type;
    private final String typeInPlural;

    public static FileType findByTypeOrTypeInPlural(String type) {
        return Arrays.stream(FileType.values())
                .filter(
                        fileType -> fileType.getType().equals(type)
                                || fileType.getTypeInPlural().equals(type)
                )
                .findFirst()
                .orElseThrow(() -> DomainPoliticalArgumentException.of(ILLEGAL_FILE_TYPE_ERROR));
    }
}
