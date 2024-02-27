package com.capick.capick.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class MemberPasswordRequest {

    @NotNull(message = "리소스 아이디를 입력해주세요.")
    private Long id;

    @NotBlank(message = "기존 비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()?])(?=\\S+$)[0-9a-zA-Z!@#$%^&*()?]{8,20}$",
            message = "기존 비밀번호는 띄어쓰기 없는 영문/숫자/특수문자(!@#$%^&*()?)를 조합하여 8자~20자리로 작성해주세요."
    )
    private String password;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()?])(?=\\S+$)[0-9a-zA-Z!@#$%^&*()?]{8,20}$",
            message = "새 비밀번호는 띄어쓰기 없는 영문/숫자/특수문자(!@#$%^&*()?)를 조합하여 8자~20자리로 작성해주세요."
    )
    private String newPassword;

    @Builder
    public MemberPasswordRequest(Long id, String password, String newPassword) {
        this.id = id;
        this.password = password;
        this.newPassword = newPassword;
    }

}
