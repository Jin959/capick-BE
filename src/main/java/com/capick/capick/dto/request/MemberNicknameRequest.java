package com.capick.capick.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class MemberNicknameRequest {

    @NotNull(message = "리소스 아이디를 입력해주세요.")
    private Long id;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[.\\w가-힇ぁ-ゔァ-ヴー々〆〤一-龥])(?=\\S+$)[.\\w가-힇ぁ-ゔァ-ヴー々〆〤一-龥]{1,20}$",
            message = "닉네임의 특수문자는 마침표(.), 밑줄(_) 만 사용하여 20자리 이하로 작성해주세요."
    )
    private String nickname;

    @Builder
    public MemberNicknameRequest(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

}
