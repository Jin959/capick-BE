package com.capick.capick.controller;

import com.capick.capick.dto.request.MemberCreateRequest;
import com.capick.capick.dto.request.MemberNicknameRequest;
import com.capick.capick.dto.request.MemberPasswordRequest;
import com.capick.capick.dto.response.MemberSimpleResponse;
import com.capick.capick.dto.response.MemberResponse;
import com.capick.capick.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("성공: 방문자는 이메일, 비밀번호, 닉네임을 입력하고 가입할 수 있다. HTTP 상태 코드 200 및 자체 응답 코드 201을 반환한다.")
    void createMember() throws Exception {
        // given
        MemberSimpleResponse response = MemberSimpleResponse.builder().build();
        when(memberService.createMember(any(MemberCreateRequest.class))).thenReturn(response);

        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("email@naver.com")
                .password("!@#$password1234")
                .nickname("nickname")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/members/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.message").value("리소스 생성에 성공했습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 회원가입 시 이메일은 필수 값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createMemberWithoutEmail() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .password("!@#$password1234")
                .nickname("nickname")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/members/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("회원가입을 위해 이메일을 입력해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 회원가입 시 이메일은 이메일 형식에 맞아야 한다. 그렇지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createMemberWithInvalidEmail() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("em@ail")
                .password("!@#$password1234")
                .nickname("nickname")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/members/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("회원가입을 위해 형식에 맞는 이메일을 입력해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: RFC 2821 상 이메일은 320자를 넘을 수 없다. HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createMemberWithTooLongEmail() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("e".repeat(64) + "@" + "e".repeat(255) + ".com")
                .password("!@#$password1234")
                .nickname("nickname")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/members/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("이메일은 320자를 넘을 수 없습니다."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 회원가입 시 비밀번호는 필수 값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void creatMemberWithoutPassword() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("email@naver.com")
                .nickname("nickname")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/members/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("회원가입을 위해 비밀번호를 입력해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 회원가입 시 비밀번호 형식은 띄어쓰기 없는 영문/숫자/특수문자(!@#$%^&*()?)를 조합하여 8자~20자이다. 그렇지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createMemberWithInvalidPassword() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("email@naver.com")
                .password("password")
                .nickname("nickname")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/members/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("회원가입을 위해 비밀번호는 띄어쓰기 없는 영문/숫자/특수문자(!@#$%^&*()?)를 조합하여 8자~20자리로 작성해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 회원가입 시 닉네임은 필수 값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createMemberWithoutNickname() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("email@naver.com")
                .password("!@#$password1234")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/members/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("회원가입을 위해 닉네임을 입력해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 회원가입 시 닉네임은 20자 이하로 특수문자는 마침표(.), 밑줄(_) 만 사용할 수 있다. 그렇지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void createMemberWithInvalidNickname() throws Exception {
        // given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("email@naver.com")
                .password("!@#$password1234")
                .nickname("nickname@@$#$")
                .build();

        // when // then
        mockMvc.perform(
                        post("/api/members/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("회원가입을 위해 닉네임의 특수문자는 마침표(.), 밑줄(_) 만 사용하여 20자리 이하로 작성해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("성공: 회원 정보를 조회한다. HTTP 상태 코드 200 및 자체 응답 코드 200 을 반환한다.")
    void getMember() throws Exception {
        // given
        MemberResponse response = MemberResponse.builder().build();
        when(memberService.getMember(anyLong())).thenReturn(response);
        int requestMemberId = 1234;

        // when // then
        mockMvc.perform(
                        get("/api/members/{memberId}", requestMemberId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("성공: 닉네임을 수정한다. HTTP 상태 코드 200 및 자체 응답 코드 200 을 반환한다.")
    void updateMemberNickname() throws Exception {
        // given
        MemberSimpleResponse response = MemberSimpleResponse.builder().build();
        when(memberService.updateMemberNickname(any(MemberNicknameRequest.class))).thenReturn(response);

        MemberNicknameRequest request = MemberNicknameRequest.builder()
                .id(1L)
                .nickname("nickname")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/members/me/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 닉네임 수정 시 회원 리소스 아이디는 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void updateMemberNicknameWithoutMemberId() throws Exception {
        // given
        MemberNicknameRequest request = MemberNicknameRequest.builder()
                .nickname("nickname")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/members/me/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리소스 아이디를 입력해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 닉네임 수정 시 닉네임은 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void updateMemberNicknameWithoutNickname() throws Exception {
        // given
        MemberNicknameRequest request = MemberNicknameRequest.builder()
                .id(1L)
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/members/me/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("닉네임을 입력해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 닉네임 수정 시 닉네임은 20자 이하로 특수문자는 마침표(.), 밑줄(_) 만 사용할 수 있다. 그렇지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void updateMemberNicknameWithInvalidNickname() throws Exception {
        // given
        MemberNicknameRequest request = MemberNicknameRequest.builder()
                .id(1L)
                .nickname("nickname@@$#$")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/members/me/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("닉네임의 특수문자는 마침표(.), 밑줄(_) 만 사용하여 20자리 이하로 작성해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("성공: 비밀번호를 수정한다. HTTP 상태 코드 200 및 자체 응답 코드 204 를 반환한다.")
    void updateMemberPassword() throws Exception {
        // given
        MemberPasswordRequest request = MemberPasswordRequest.builder()
                .id(1L)
                .password("pass!*word13")
                .newPassword("new!*password13")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/members/me/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("204"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 비밀번호 수정 시 회원 리소스 아이디는 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void updateMemberPasswordWithoutMemberId() throws Exception {
        // given
        MemberPasswordRequest request = MemberPasswordRequest.builder()
                .password("pass!*word13")
                .newPassword("new!*password13")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/members/me/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리소스 아이디를 입력해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 비밀번호 수정 시 기존 비밀번호는 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void updateMemberPasswordWithoutPassword() throws Exception {
        // given
        MemberPasswordRequest request = MemberPasswordRequest.builder()
                .id(1L)
                .newPassword("new!*password13")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/members/me/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("기존 비밀번호를 입력해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 비밀번호 수정 시 새 비밀번호는 필수값이다. 입력하지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void updateMemberPasswordWithoutNewPassword() throws Exception {
        // given
        MemberPasswordRequest request = MemberPasswordRequest.builder()
                .id(1L)
                .password("pass!*word13")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/members/me/password")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("새 비밀번호를 입력해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("예외: 비밀번호 수정 시 형식은 띄어쓰기 없는 영문/숫자/특수문자(!@#$%^&*()?)를 조합하여 8자~20자이다. 그렇지 않으면 HTTP 상태 코드 400 및 자체 응답 코드 400을 반환한다.")
    void updateMemberPasswordWithInvalidPassword() throws Exception {
        // given
        MemberPasswordRequest requestWithInvalidPassword = MemberPasswordRequest.builder()
                .id(1L)
                .password("pass!*word")
                .newPassword("new!*password13")
                .build();
        MemberPasswordRequest requestWithInvalidNewPassword = MemberPasswordRequest.builder()
                .id(1L)
                .password("pass!*word13")
                .newPassword("new!*password")
                .build();

        // when // then
        mockMvc.perform(
                        patch("/api/members/me/password")
                                .content(objectMapper.writeValueAsString(requestWithInvalidPassword))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("기존 비밀번호는 띄어쓰기 없는 영문/숫자/특수문자(!@#$%^&*()?)를 조합하여 8자~20자리로 작성해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
        mockMvc.perform(
                        patch("/api/members/me/password")
                                .content(objectMapper.writeValueAsString(requestWithInvalidNewPassword))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("새 비밀번호는 띄어쓰기 없는 영문/숫자/특수문자(!@#$%^&*()?)를 조합하여 8자~20자리로 작성해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("성공: 회원 탈퇴를 요청 한다. HTTP 상태 코드 200 및 자체 응답 코드 204 를 반환한다.")
    void deleteMember() throws Exception {
        // given
        int requestMemberId = 1234;

        // when // then
        mockMvc.perform(
                        delete("/api/members/{memberId}", requestMemberId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("204"))
                .andExpect(jsonPath("$.message").value("리소스 삭제에 성공했습니다."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print());
    }

}