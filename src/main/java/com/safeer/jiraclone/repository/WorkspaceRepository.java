package com.safeer.jiraclone.repository;

import com.safeer.jiraclone.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, String> {

}
