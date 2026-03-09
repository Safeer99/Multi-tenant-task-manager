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
@Table(name = "members", uniqueConstraints = { @UniqueConstraint(columnNames = {"userId", "workspaceId"}) })
public class Member {

    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name="userId", updatable = false, nullable = false)
    private String userId;

    @Column(name="workspaceId", updatable = false, nullable = false)
    private String workspaceId;

    @Column(nullable = false)
    private Role role;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

}

