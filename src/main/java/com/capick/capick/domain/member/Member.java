package com.capick.capick.domain.member;

import com.capick.capick.domain.common.BaseEntity;
import com.capick.capick.domain.common.BaseStatus;
import com.capick.capick.domain.common.Location;
import com.capick.capick.exception.DuplicateResourceException;
import com.capick.capick.exception.UnauthorizedException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.capick.capick.dto.ApiResponseStatus.INCORRECT_PASSWORD_ERROR;
import static com.capick.capick.dto.ApiResponseStatus.NOT_CHANGED_PASSWORD;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 320)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Embedded
    private Profile profile;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "prefer_town_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "prefer_town_longitude")),
            @AttributeOverride(name = "state", column = @Column(name = "prefer_town_state")),
            @AttributeOverride(name = "city", column = @Column(name = "prefer_town_city")),
            @AttributeOverride(name = "street", column = @Column(name = "prefer_town_street")),
            @AttributeOverride(name = "number", column = @Column(name = "prefer_town_number"))
    })
    private Location preferTown;

    @Builder
    private Member(String email, String password, String nickname, Profile profile, Location location) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profile = profile;
        this.preferTown = location;
    }

    public void delete() {
        this.status = BaseStatus.INACTIVE;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password, String newPassword) {
        if (newPassword.equals(this.password)) {
            throw DuplicateResourceException.of(NOT_CHANGED_PASSWORD);
        }
        if (!password.equals(this.password)) {
            throw UnauthorizedException.of(INCORRECT_PASSWORD_ERROR);
        }
        this.password = newPassword;
    }
}
