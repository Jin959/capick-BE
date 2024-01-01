package com.capick.capick.domain.member;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Builder
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Column(name = "profile_image_url")
    private String imageUrl;

    @Column(length = 100)
    private String introduction;

}
