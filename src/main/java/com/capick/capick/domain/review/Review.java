package com.capick.capick.domain.review;

import com.capick.capick.domain.cafe.Cafe;
import com.capick.capick.domain.common.BaseEntity;
import com.capick.capick.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private int priceIndex;

    @Column(nullable = false)
    private int spaceIndex;

    @Column(nullable = false)
    private int noiseIndex;

}
