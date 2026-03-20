package com.safeer.jiraclone.controllers;

import com.safeer.jiraclone.dto.AddTaskDto;
import com.safeer.jiraclone.dto.BulkUpdateTasksDto;
import com.safeer.jiraclone.dto.TasksProjectAssigneeDetailsDto;
import com.safeer.jiraclone.dto.UserDetailsDto;
import com.safeer.jiraclone.entity.Task;
import com.safeer.jiraclone.service.AuthService;
import com.safeer.jiraclone.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private AuthService authService;

    @Autowired
    private TaskService taskService;

    @GetMapping("/")
    public ResponseEntity<List<TasksProjectAssigneeDetailsDto>> getAllTasks(@RequestBody Map<String, String> filters, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.ok(taskService.filterTasks(userDetails.getId(), filters));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TasksProjectAssigneeDetailsDto> getTask(@PathVariable String id, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.ok(taskService.getTask(id , userDetails.getId()));
    }

    @PostMapping("/")
    public ResponseEntity<Task> createTask(@RequestBody @Valid AddTaskDto addTaskDto, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(userDetails.getId(), addTaskDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody Map<String, Object> updates, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.ok(taskService.updateTask(id, userDetails.getId(), updates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);
        taskService.deleteTask(id, userDetails.getId());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/bulk-update")
    public ResponseEntity<List<Task>> bulkUpdateTasks(@RequestBody List<BulkUpdateTasksDto> tasks, @RequestHeader HttpHeaders headers){

        UserDetailsDto userDetails = authService.getCurrentUser(headers);

        return ResponseEntity.ok(taskService.bulkUpdateTasks(userDetails.getId(), tasks));
    }

}

