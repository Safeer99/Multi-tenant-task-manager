package com.safeer.jiraclone.controllers;

import com.safeer.jiraclone.dto.AddProjectDto;
import com.safeer.jiraclone.dto.UserDetailsDto;
import com.safeer.jiraclone.entity.Project;
import com.safeer.jiraclone.service.AuthService;
import com.safeer.jiraclone.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/")
    public ResponseEntity<List<Project>> getAllProjects(@RequestBody Map<String, String> data, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.ok(projectService.getAllProjects(userDetails.getId(), data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable String id, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.ok(projectService.getProjectById(id, userDetails.getId()));
    }

    @PostMapping("/")
    public ResponseEntity<Project> createProject(@RequestBody @Valid AddProjectDto addProjectDto, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(userDetails.getId(),addProjectDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable String id, @RequestBody Map<String, Object> updates, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.ok(projectService.updateProject(id, userDetails.getId(), updates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);
        projectService.deleteProject(id, userDetails.getId());

        return ResponseEntity.noContent().build();
    }
}
