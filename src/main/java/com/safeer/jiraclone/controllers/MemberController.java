package com.safeer.jiraclone.controllers;

import com.safeer.jiraclone.dto.UserDetailsDto;
import com.safeer.jiraclone.dto.UserMemberDetailsDto;
import com.safeer.jiraclone.entity.Member;
import com.safeer.jiraclone.service.AuthService;
import com.safeer.jiraclone.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public ResponseEntity<List<UserMemberDetailsDto>> getAllMembers(@RequestBody Map<String, String> data, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.ok(memberService.getAllMembers(userDetails.getId(), data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable String id, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);
        memberService.deleteMember(id, userDetails.getId());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Member> updateMemberRole(@PathVariable String id, @RequestBody Map<String, String> updates, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.ok(memberService.updateMemberRole(id, userDetails.getId(), updates));
    }
}
