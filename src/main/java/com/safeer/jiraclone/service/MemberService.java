package com.safeer.jiraclone.service;

import com.safeer.jiraclone.dto.UserMemberDetailsDto;
import com.safeer.jiraclone.entity.Member;
import com.safeer.jiraclone.entity.Role;
import com.safeer.jiraclone.entity.User;
import com.safeer.jiraclone.entity.Workspace;
import com.safeer.jiraclone.repository.MemberRepository;
import com.safeer.jiraclone.repository.UserRepository;
import com.safeer.jiraclone.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private UserRepository userRepository;


    public List<UserMemberDetailsDto> getAllMembers(String userId, Map<String, String> data) {

        String workspaceId = data.get("workspaceId");

        if(workspaceId == null){
            throw new IllegalArgumentException("WorkspaceId not found!");
        }

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, workspaceId);

        if(member == null){
            throw new IllegalArgumentException("Unauthorized");
        }

        List<Member> membersList = memberRepository.findAllByWorkspaceId(member.getWorkspaceId());

        List<String> userIds = membersList.stream().map(Member::getUserId).toList();

        List<User> usersList = userRepository.findAllById(userIds);

        return membersList.stream().map(value -> {
            User user = usersList.stream().filter(v -> v.getId().equals(value.getUserId())).toList().getFirst();

            return UserMemberDetailsDto.builder()
                    .memberId(value.getId())
                    .workspaceId(value.getWorkspaceId())
                    .userId(value.getUserId())
                    .role(value.getRole())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .build();

        }).toList();
    }

    public void deleteMember(String id, String userId) {

        Member memberToDelete = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Member not found with id: "+id));

        Workspace workspace = workspaceRepository.findById(memberToDelete.getWorkspaceId()).orElseThrow(() -> new IllegalArgumentException("Workspace not found with id: "+id));

        if(workspace.getUserId().equals(memberToDelete.getUserId())){
            throw new IllegalArgumentException("Unauthorized");
        }

        List<Member> allMembersInWorkspace = memberRepository.findAllByWorkspaceId(memberToDelete.getWorkspaceId());

        if(allMembersInWorkspace.toArray().length == 1){
            throw new IllegalArgumentException("Cannot delete the only member");
        }

        Member currentMember = memberRepository.findByUserIdAndWorkspaceId(userId, memberToDelete.getWorkspaceId());

        if(currentMember == null || currentMember.getId().equals(memberToDelete.getId()) || currentMember.getRole() != Role.ADMIN){
            throw new IllegalArgumentException("Unauthorized");
        }

        memberRepository.deleteById(id);
    }


    public Member updateMemberRole(String id, String userId, Map<String, String> updates) {

        Role role = Role.valueOf(updates.get("role"));

//        if(role != null){
//            throw new IllegalArgumentException("Role not found!!!");
//        }

        Member memberToUpdate = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Member not found with id: "+id));

        List<Member> allMembersInWorkspace = memberRepository.findAllByWorkspaceId(memberToUpdate.getWorkspaceId());

        if(allMembersInWorkspace.toArray().length == 1){
            throw new IllegalArgumentException("Cannot downgrade the only member");
        }

        Member currentMember = memberRepository.findByUserIdAndWorkspaceId(userId, memberToUpdate.getWorkspaceId());

        if(currentMember == null || currentMember.getId().equals(memberToUpdate.getId()) || currentMember.getRole() != Role.ADMIN){
            throw new IllegalArgumentException("Unauthorized");
        }

        memberToUpdate.setRole(role);

        return memberRepository.save(memberToUpdate);
    }
}
