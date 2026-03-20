package com.safeer.jiraclone.dto;

import com.safeer.jiraclone.entity.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddTaskDto {

    private String workspaceId;

    private String projectId;

    private String assigneeId;

    private String name;

    private String description;

    private LocalDateTime dueDate;

    private Status status;

}
