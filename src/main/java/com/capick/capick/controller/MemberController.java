package com.capick.capick.controller;

import com.capick.capick.dto.ApiResponse;
import com.capick.capick.dto.request.MemberCreateRequest;
import com.capick.capick.dto.request.MemberPasswordRequest;
import com.capick.capick.dto.request.MemberNicknameRequest;
import com.capick.capick.dto.response.MemberSimpleResponse;
import com.capick.capick.dto.response.MemberResponse;
import com.capick.capick.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.capick.capick.dto.ApiResponseStatus.NO_DATA;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/new")
    public ApiResponse<MemberSimpleResponse> createMember(@Valid @RequestBody MemberCreateRequest memberCreateRequest) {
        return ApiResponse.isCreated(memberService.createMember(memberCreateRequest));
    }

    @GetMapping("/{memberId}")
    public ApiResponse<MemberResponse> getMember(@PathVariable("memberId") Long memberId) {
        return ApiResponse.ok(memberService.getMember(memberId));
    }

    @PatchMapping("/me/nickname")
    public ApiResponse<MemberSimpleResponse> updateMemberNickname(@Valid @RequestBody MemberNicknameRequest memberNicknameRequest) {
        return ApiResponse.ok(memberService.updateMemberNickname(memberNicknameRequest));
    }

    @PatchMapping("/me/password")
    public ApiResponse<Void> updateMemberPassword(@Valid @RequestBody MemberPasswordRequest memberPasswordRequest) {
        memberService.updateMemberPassword(memberPasswordRequest);
        return ApiResponse.of(NO_DATA);
    }

    @DeleteMapping("/{memberId}")
    public ApiResponse<Void> deleteMember(@PathVariable("memberId") Long memberId) {
        memberService.deleteMember(memberId);
        return ApiResponse.isDeleted();
    }

}
