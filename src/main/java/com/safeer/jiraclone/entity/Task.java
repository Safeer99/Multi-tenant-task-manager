package com.safeer.jiraclone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "workspace_id" ,updatable = false, nullable = false)
    private String workspaceId;

    @Column(nullable = false)
    private String name;

    @Column(name = "project_id", updatable = false, nullable = false)
    private String projectId;

    @Column(name="assignee_id",updatable = false, nullable = false)
    private String assigneeId;

    private String description;

    @Column(name="due_date",nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private int position;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
