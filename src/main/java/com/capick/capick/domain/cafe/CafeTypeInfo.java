package com.capick.capick.domain.cafe;

import com.capick.capick.domain.review.Review;
import com.capick.capick.exception.DomainLogicalException;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.*;

import static com.capick.capick.dto.ApiResponseStatus.LACK_OF_ACCUMULATED_CAFE_TYPE_INDEX;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CafeTypeInfo {

    @Column(nullable = false)
    private int coffeeIndex;

    @Column(nullable = false)
    private int spaceIndex;

    @Column(nullable = false)
    private int priceIndex;

    @Column(nullable = false)
    private int noiseIndex;

    @Enumerated(EnumType.STRING)
    private CafeType cafeType;

    protected void updateCafeType(Review review) {
        List<Integer> indexes = List.of(coffeeIndex, spaceIndex, priceIndex, noiseIndex);

        preventIndexOverflow(indexes);
        updateIndexes(review);

        Map<String, Integer> indexMap = createIndexMap();
        Integer maxIndexValue = Collections.max(indexMap.values());

        if (hasMaxIndex(indexMap, maxIndexValue)) {
            String maxIndexKey = findMaxIndexName(indexMap);
            cafeType = CafeType.findByIndexName(maxIndexKey);
        }
    }

    protected void deductCafeTypeIndex(Review review) {
        if (isAccumulatedTypeIndexLessThanTypeIndexOf(review)) {
            throw DomainLogicalException.of(LACK_OF_ACCUMULATED_CAFE_TYPE_INDEX);
        }
        coffeeIndex -= review.getCoffeeIndex();
        spaceIndex -= review.getSpaceIndex();
        priceIndex -= review.getPriceIndex();
        noiseIndex -= review.getNoiseIndex();
    }

    private void preventIndexOverflow(List<Integer> indexes) {
        int overflowBoundary = Integer.MAX_VALUE - 10000;

        if (indexes.stream().anyMatch(index -> index > overflowBoundary)) {
            coffeeIndex /= 2;
            spaceIndex /= 2;
            priceIndex /= 2;
            noiseIndex /= 2;
        }
    }

    private void updateIndexes(Review review) {
        coffeeIndex += review.getCoffeeIndex();
        spaceIndex += review.getSpaceIndex();
        priceIndex += review.getPriceIndex();
        noiseIndex += review.getNoiseIndex();
    }

    private Map<String, Integer> createIndexMap() {
        Map<String, Integer> indexMap = new HashMap<>();
        indexMap.put("coffeeIndex", coffeeIndex);
        indexMap.put("spaceIndex", spaceIndex);
        indexMap.put("priceIndex", priceIndex);
        indexMap.put("noiseIndex", noiseIndex);
        return indexMap;
    }

    private boolean hasMaxIndex(Map<String, Integer> indexMap, Integer maxIndexValue) {
        return countMaxIndex(indexMap, maxIndexValue) == 1;
    }

    private long countMaxIndex(Map<String, Integer> indexMap, Integer maxIndexValue) {
        return indexMap.keySet().stream()
                .filter(indexKey -> indexMap.get(indexKey).equals(maxIndexValue))
                .count();
    }

    private String findMaxIndexName(Map<String, Integer> indexMap) {
        return indexMap.keySet().stream()
                .max(Comparator.comparing(indexMap::get))
                .orElseGet(() -> "none");
    }

    private boolean isAccumulatedTypeIndexLessThanTypeIndexOf(Review review) {
        return coffeeIndex < review.getCoffeeIndex() | spaceIndex < review.getSpaceIndex()
                | priceIndex < review.getPriceIndex() | noiseIndex < review.getNoiseIndex();
    }

}
