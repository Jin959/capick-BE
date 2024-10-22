package com.capick.capick.domain.history.storage;

import com.capick.capick.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StorageOrphanFileHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Enumerated(EnumType.STRING)
    private FileDomain domain;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private boolean isHardDeleted = false;

    @Builder
    private StorageOrphanFileHistory(String fileName, FileType fileType, FileDomain domain, String url) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.domain = domain;
        this.url = url;
    }

}
