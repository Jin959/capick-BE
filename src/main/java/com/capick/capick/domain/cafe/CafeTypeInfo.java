package com.capick.capick.domain.cafe;

import com.capick.capick.domain.review.Review;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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
        this.updateIndexes(review);
        Map<String, Integer> indexMap = createIndexMap();

        Integer maxIndexValue = Collections.max(indexMap.values());

        if (hasMaxIndex(indexMap, maxIndexValue)) {
            String maxIndexKey = findMaxIndexName(indexMap);
            this.cafeType = CafeType.findByIndexName(maxIndexKey);
        }
    }

    private void updateIndexes(Review review) {
        this.coffeeIndex += review.getCoffeeIndex();
        this.spaceIndex += review.getSpaceIndex();
        this.priceIndex += review.getPriceIndex();
        this.noiseIndex += review.getNoiseIndex();
    }

    private Map<String, Integer> createIndexMap() {
        Map<String, Integer> indexMap = new HashMap<>();
        indexMap.put("coffeeIndex", this.coffeeIndex);
        indexMap.put("spaceIndex", this.spaceIndex);
        indexMap.put("priceIndex", this.priceIndex);
        indexMap.put("noiseIndex", this.noiseIndex);
        return indexMap;
    }

    private boolean hasMaxIndex(Map<String, Integer> indexMap, Integer maxIndexValue) {
        return countMaxIndex(indexMap, maxIndexValue) < indexMap.size();
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

}
