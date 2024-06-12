package com.capick.capick.domain.cafe;

import com.capick.capick.domain.review.Review;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.*;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CafeThemeInfo {

    @Column(nullable = false)
    private int normalCount;

    @Column(nullable = false)
    private int vibeCount;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int petCount;

    @Column(nullable = false)
    private int hobbyCount;

    @Column(nullable = false)
    private int studyCount;

    @Column(nullable = false)
    private int kidsCount;

    @Column(nullable = false)
    private int etcCount;

    @Enumerated(EnumType.STRING)
    private CafeTheme cafeTheme;

    protected void updateCafeTheme(Review review) {
        List<Integer> themeCounts
                = List.of(normalCount, vibeCount, viewCount, petCount, hobbyCount, studyCount, kidsCount, etcCount);

        preventCountOverflow(themeCounts);
        updateThemeCount(review.getTheme());

        Map<String, Integer> themeCountMap = createThemeCountMap();
        Integer maxCountValue = Collections.max(themeCountMap.values());

        if (hasMaxThemeCount(themeCountMap, maxCountValue)) {
            String maxThemeKey = findMaxThemeName(themeCountMap);
            cafeTheme = CafeTheme.findByThemeName(maxThemeKey);
        }
    }

    private void preventCountOverflow(List<Integer> themeCounts) {
        int overflowBoundary = Integer.MAX_VALUE - 10000;

        if (themeCounts.stream().anyMatch(count -> count > overflowBoundary)) {
            normalCount /= 2;
            vibeCount /= 2;
            viewCount /= 2;
            petCount /= 2;
            hobbyCount /= 2;
            studyCount /= 2;
            kidsCount /= 2;
            etcCount /= 2;
        }
    }

    private void updateThemeCount(String theme) {
        switch (theme) {
            case "normal":
                normalCount++;
                break;
            case "vibe":
                vibeCount++;
                break;
            case "view":
                viewCount++;
                break;
            case "pet":
                petCount++;
                break;
            case "hobby":
                hobbyCount++;
                break;
            case "study":
                studyCount++;
                break;
            case "kids":
                kidsCount++;
                break;
            default:
                etcCount++;
        }
    }

    private Map<String, Integer> createThemeCountMap() {
        Map<String, Integer> themeCountMap = new HashMap<>();
        themeCountMap.put("normal", normalCount);
        themeCountMap.put("vibe", vibeCount);
        themeCountMap.put("view", viewCount);
        themeCountMap.put("pet", petCount);
        themeCountMap.put("hobby", hobbyCount);
        themeCountMap.put("study", studyCount);
        themeCountMap.put("kids", kidsCount);
        themeCountMap.put("etc", etcCount);
        return themeCountMap;
    }

    private boolean hasMaxThemeCount(Map<String, Integer> themeCountMap, Integer maxCountValue) {
        return countMaxTheme(themeCountMap, maxCountValue) == 1;
    }

    private long countMaxTheme(Map<String, Integer> themeCountMap, Integer maxCountValue) {
        return themeCountMap.keySet().stream()
                .filter(themeKey -> themeCountMap.get(themeKey).equals(maxCountValue))
                .count();
    }

    private String findMaxThemeName(Map<String, Integer> themeCountMap) {
        return themeCountMap.keySet().stream()
                .max(Comparator.comparing(themeCountMap::get))
                .orElseGet(() -> "etc");
    }

}
