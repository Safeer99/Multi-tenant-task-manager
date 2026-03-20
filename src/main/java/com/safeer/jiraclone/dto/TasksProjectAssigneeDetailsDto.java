package com.safeer.jiraclone.dto;

import com.safeer.jiraclone.entity.Project;
import com.safeer.jiraclone.entity.Task;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TasksProjectAssigneeDetailsDto {

    private Task task;
    private Project project;
    private UserMemberDetailsDto assignee;

}
