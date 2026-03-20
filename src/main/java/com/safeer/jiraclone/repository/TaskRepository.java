package com.safeer.jiraclone.repository;

import com.safeer.jiraclone.entity.Status;
import com.safeer.jiraclone.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, String>, JpaSpecificationExecutor<Task> {

    Task findFirstByWorkspaceIdAndStatusOrderByPositionAsc(String workspaceId, Status status);

}
