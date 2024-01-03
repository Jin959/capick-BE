package com.capick.capick.controller;

import com.capick.capick.dto.ApiResponse;
import com.capick.capick.dto.request.MemberCreateRequest;
import com.capick.capick.dto.request.MemberUpdateRequest;
import com.capick.capick.dto.response.MemberCreateResponse;
import com.capick.capick.dto.response.MemberResponse;
import com.capick.capick.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/new")
    public ApiResponse<MemberCreateResponse> createMember(@Valid @RequestBody MemberCreateRequest memberCreateRequest) {
        return ApiResponse.ok(memberService.createMember(memberCreateRequest));
    }

    @GetMapping("/{memberId}")
    public ApiResponse<MemberResponse> getMember(@PathVariable("memberId") Long memberId) {
        return ApiResponse.ok(memberService.getMember(memberId));
    }

    @PatchMapping("/me")
    public ApiResponse<MemberResponse> updateMemberInfo(@Valid @RequestBody MemberUpdateRequest memberUpdateRequest) {
        return ApiResponse.ok(memberService.updateMemberInfo(memberUpdateRequest));
    }

}
