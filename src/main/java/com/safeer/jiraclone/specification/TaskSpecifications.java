package com.safeer.jiraclone.specification;

import com.safeer.jiraclone.entity.Status;
import com.safeer.jiraclone.entity.Task;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TaskSpecifications {

    public static Specification<Task> hasWorkspaceId(String workspaceId){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("workspaceId"), workspaceId);
    }

    public static Specification<Task> hasProjectId(String projectId){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("projectId"), projectId);
    }

    public static Specification<Task> hasAssigneeId(String assigneeId){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("assigneeId"), assigneeId);
    }

    public static Specification<Task> hasStatus(Status status){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Task> containsName(String search){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), "%"+search+"%");
    }

    public static Specification<Task> hasDueDate(LocalDateTime dueDate){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("dueDate"), dueDate);
    }

    public static Specification<Task> orderBy(String field, boolean isAsc) {
        return (root, query, criteriaBuilder) -> {
            if (isAsc) {
                query.orderBy(criteriaBuilder.asc(root.get(field)));
            } else {
                query.orderBy(criteriaBuilder.desc(root.get(field)));
            }
            return null;
        };
    }

}
