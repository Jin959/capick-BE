package com.capick.capick.domain.history.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {

    IMAGE("이미지 파일", "image", "images"),
    VIDEO("동영상 파일", "video", "videos");

    private final String text;
    private final String type;
    private final String typeInPlural;

}
