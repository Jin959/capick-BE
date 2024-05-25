package com.capick.capick.domain.review;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.common.BaseEntity;
import com.capick.capick.domain.member.Member;
import com.capick.capick.exception.DomainPoliticalArgumentException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static com.capick.capick.dto.ApiResponseStatus.REVIEW_WITH_CAFE_TYPE_INDEX_OUT_OF_RANGE;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cafe cafe;

    @Column(nullable = false, length = 20)
    private String visitPurpose;

    @Column(length = 300)
    private String content;

    @Column(nullable = false)
    private String menu;

    @Column(nullable = false)
    private int coffeeIndex;

    @Column(nullable = false)
    private int spaceIndex;

    @Column(nullable = false)
    private int priceIndex;

    @Column(nullable = false)
    private int noiseIndex;

    @Column(nullable = false)
    private String theme;

    private LocalDateTime registeredAt;

    @Builder
    private Review(Member writer, Cafe cafe, String visitPurpose, String content, String menu,
                   int coffeeIndex, int spaceIndex, int priceIndex, int noiseIndex, String theme, LocalDateTime registeredAt) {
        this.writer = writer;
        this.cafe = cafe;
        this.visitPurpose = visitPurpose;
        this.content = content;
        this.menu = menu;
        this.coffeeIndex = coffeeIndex;
        this.spaceIndex = spaceIndex;
        this.priceIndex = priceIndex;
        this.noiseIndex = noiseIndex;
        this.theme = theme;
        this.registeredAt = registeredAt;
    }

    public void updateIndexes(int coffeeIndex, int spaceIndex, int priceIndex, int noiseIndex) {
        List<Integer> indexes = List.of(coffeeIndex, spaceIndex, priceIndex, noiseIndex);
        boolean isIndexOutOfRange = indexes.stream().anyMatch(index -> index < 1 || index > 5);
        if (isIndexOutOfRange) {
            throw DomainPoliticalArgumentException.of(REVIEW_WITH_CAFE_TYPE_INDEX_OUT_OF_RANGE);
        }

        this.coffeeIndex = coffeeIndex;
        this.spaceIndex = spaceIndex;
        this.priceIndex = priceIndex;
        this.noiseIndex = noiseIndex;
    }

}
