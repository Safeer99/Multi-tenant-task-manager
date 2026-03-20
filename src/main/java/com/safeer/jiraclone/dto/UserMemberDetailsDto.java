package com.safeer.jiraclone.dto;

import com.safeer.jiraclone.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMemberDetailsDto {

    private String memberId;
    private String userId;
    private String workspaceId;
    private String username;
    private String email;
    private Role role;
}
