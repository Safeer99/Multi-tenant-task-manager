package com.safeer.jiraclone.dto;

import com.safeer.jiraclone.entity.Status;
import lombok.Data;

@Data
public class BulkUpdateTasksDto {

    private String id;
    private int position;
    private Status status;

}
