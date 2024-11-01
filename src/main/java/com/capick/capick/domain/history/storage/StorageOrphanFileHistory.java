package com.capick.capick.domain.history.storage;

import com.capick.capick.domain.common.BaseEntity;
import com.capick.capick.exception.DomainPoliticalArgumentException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.capick.capick.dto.ApiResponseStatus.*;

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

    public static StorageOrphanFileHistory create(String fileName, FileType fileType, FileDomain domain, String url) {
        return StorageOrphanFileHistory.builder()
                .fileName(fileName)
                .fileType(fileType)
                .domain(domain)
                .url(url)
                .build();
    }

    public static List<StorageOrphanFileHistory> createStorageOrphanFileHistories(
            List<String> fileNames, List<String> fileTypes, List<String> domains, List<String> urls) {
        int numberOfFiles = urls.size();

        if (numberOfFiles > 10) {
            throw DomainPoliticalArgumentException.of(NUMBER_OF_ORPHAN_FILE_EXCEEDED);
        }

        boolean isUrlsDuplicate = urls.stream()
                .distinct().count() != numberOfFiles;
        if (isUrlsDuplicate) {
            throw DomainPoliticalArgumentException.of(DUPLICATE_REQUEST_FILE);
        }

        List<StorageOrphanFileHistory> orphanFiles = new ArrayList<>();
        for (int fileNumber = 0; fileNumber < numberOfFiles; fileNumber++) {
            orphanFiles.add(StorageOrphanFileHistory.create(
                    fileNames.get(fileNumber), FileType.findByTypeOrTypeInPlural(fileTypes.get(fileNumber)),
                    FileDomain.findByDomainOrDomainInPlural(domains.get(fileNumber)), urls.get(fileNumber)
            ));
        }

        return orphanFiles;
    }

}
