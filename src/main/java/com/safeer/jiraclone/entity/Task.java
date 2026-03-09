package com.safeer.jiraclone.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(updatable = false, nullable = false)
    private String workspaceId;

    @Column(nullable = false)
    private String name;

    @Column(updatable = false, nullable = false)
    private String projectId;

    @Column(updatable = false, nullable = false)
    private String assigneeId;

    private String description;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private int position;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
