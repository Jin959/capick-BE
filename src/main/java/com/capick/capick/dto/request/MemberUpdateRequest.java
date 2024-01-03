package com.capick.capick.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class MemberUpdateRequest {

    private Long id;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()?])(?=\\S+$)[0-9a-zA-Z!@#$%^&*()?]{8,20}$",
            message = "비밀번호는 띄어쓰기 없는 영문/숫자/특수문자(!@#$%^&*()?)를 조합하여 8자~20자리로 작성해주세요."
    )
    private String password;

    @Pattern(
            regexp = "^(?=.*[.\\w가-힇ぁ-ゔァ-ヴー々〆〤一-龥])(?=\\S+$)[.\\w가-힇ぁ-ゔァ-ヴー々〆〤一-龥]{1,20}$",
            message = "회원가입을 위해 닉네임의 특수문자는 마침표(.), 밑줄(_) 만 사용하여 20자리 이하로 작성해주세요."
    )
    private String nickname;

}
