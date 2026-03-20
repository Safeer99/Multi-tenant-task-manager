package com.safeer.jiraclone.controllers;

import com.safeer.jiraclone.dto.AddWorkspaceDto;
import com.safeer.jiraclone.dto.UserDetailsDto;
import com.safeer.jiraclone.entity.Workspace;
import com.safeer.jiraclone.service.AuthService;
import com.safeer.jiraclone.service.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workspace")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public ResponseEntity<List<Workspace>> getAllWorkspaces(@RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.ok(workspaceService.getAllWorkspaces(userDetails.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workspace> getWorkspaceById(@PathVariable String id, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.ok(workspaceService.getWorkspaceById(id, userDetails.getId()));
    }

    @PostMapping("/")
    public ResponseEntity<Workspace> createWorkspace(@RequestBody @Valid AddWorkspaceDto addWorkspaceDto, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.status(HttpStatus.CREATED).body(workspaceService.createWorkspace(addWorkspaceDto, userDetails.getId()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Workspace> updateWorkspace(@PathVariable String id, @RequestBody Map<String, Object> updates, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.ok(workspaceService.updateWorkspace(id, userDetails.getId(), updates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable String id, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);
        workspaceService.deleteWorkspace(id, userDetails.getId());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reset-invite-code")
    public ResponseEntity<Workspace> resetInviteCode(@PathVariable String id, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.ok(workspaceService.resetInviteCode(id, userDetails.getId()));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Workspace> joinWorkspace(@PathVariable String id, @RequestBody @Valid int code, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.ok(workspaceService.joinWorkspace(id, userDetails.getId(), code));
    }
}
