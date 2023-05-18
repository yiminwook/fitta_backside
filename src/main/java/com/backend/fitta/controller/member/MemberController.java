package com.backend.fitta.controller.member;

import com.backend.fitta.dto.Member.FindByEmailResponse;
import com.backend.fitta.dto.Member.SignUpRequest;
import com.backend.fitta.dto.Member.UpdateMemberRequest;
import com.backend.fitta.exception.MemberNotFoundException;
import com.backend.fitta.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Long> saveMember(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.save(request));
    }

    @PutMapping("/{memberEmail}")
    public ResponseEntity<Long> updateMember(@PathVariable String memberEmail, @Valid @RequestBody UpdateMemberRequest request) {
        memberService.findByEmail(memberEmail).orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));
        return ResponseEntity.ok(memberService.update(memberEmail, request));
    }

    @GetMapping("/{memberEmail}")
    public ResponseEntity<FindByEmailResponse> findMember(@PathVariable String memberEmail) {
        memberService.findByEmail(memberEmail).orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));
        return ResponseEntity.ok(memberService.findMember(memberEmail));
    }

    @DeleteMapping("/{memberEmail}")
    public ResponseEntity<Void> deleteMember(@PathVariable String memberEmail) {
        memberService.findByEmail(memberEmail).orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));
        return ResponseEntity.noContent().build();
    }
}
