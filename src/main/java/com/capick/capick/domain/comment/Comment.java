package com.capick.capick.domain.comment;

import com.capick.capick.domain.common.BaseEntity;
import com.capick.capick.domain.member.Member;
import com.capick.capick.domain.review.Review;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @Column(nullable = false, length = 300)
    private String content;

}
