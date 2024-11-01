package com.capick.capick.repository.history.storage;

import com.capick.capick.domain.history.storage.StorageOrphanFileHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageOrphanFileHistoryRepository extends JpaRepository<StorageOrphanFileHistory, Long> {

    boolean existsByUrlInAndIsHardDeleted(List<String> urls, boolean isHardDeleted);

}
