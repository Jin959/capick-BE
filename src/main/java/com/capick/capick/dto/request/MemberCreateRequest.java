package com.capick.capick.dto.request;

import com.capick.capick.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
public class MemberCreateRequest {

    @NotBlank(message = "회원가입을 위해 이메일을 입력해주세요.")
    @Pattern(
            regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$",
            message = "회원가입을 위해 형식에 맞는 이메일을 입력해주세요."
    )
    @Size(max = 320, message = "이메일은 320자를 넘을 수 없습니다.")
    private String email;

    @NotBlank(message = "회원가입을 위해 비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()?])(?=\\S+$)[0-9a-zA-Z!@#$%^&*()?]{8,20}$",
            message = "회원가입을 위해 비밀번호는 띄어쓰기 없는 영문/숫자/특수문자(!@#$%^&*()?)를 조합하여 8자~20자리로 작성해주세요."
    )
    private String password;

    @NotBlank(message = "회원가입을 위해 닉네임을 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[.\\w가-힇ぁ-ゔァ-ヴー々〆〤一-龥])(?=\\S+$)[.\\w가-힇ぁ-ゔァ-ヴー々〆〤一-龥]{1,20}$",
            message = "회원가입을 위해 닉네임의 특수문자는 마침표(.), 밑줄(_) 만 사용하여 20자리 이하로 작성해주세요."
    )
    private String nickname;

    @Builder
    public MemberCreateRequest(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

}
