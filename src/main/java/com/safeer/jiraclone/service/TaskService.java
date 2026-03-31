package com.safeer.jiraclone.service;

import com.safeer.jiraclone.dto.AddTaskDto;
import com.safeer.jiraclone.dto.BulkUpdateTasksDto;
import com.safeer.jiraclone.dto.TasksProjectAssigneeDetailsDto;
import com.safeer.jiraclone.dto.UserMemberDetailsDto;
import com.safeer.jiraclone.entity.*;
import com.safeer.jiraclone.repository.MemberRepository;
import com.safeer.jiraclone.repository.ProjectRepository;
import com.safeer.jiraclone.repository.TaskRepository;
import com.safeer.jiraclone.repository.UserRepository;
import com.safeer.jiraclone.specification.TaskSpecifications;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TaskService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    public List<TasksProjectAssigneeDetailsDto> filterTasks(String userId, Map<String, String> filters){

        String workspaceId = filters.get("workspaceId");

        if(workspaceId == null){
            throw new IllegalArgumentException("WorkspaceId not found");
        }

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, workspaceId);

        if(member == null){
            throw new IllegalArgumentException("Unauthorized");
        }

        Specification<Task> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("workspaceId"), filters.get("workspaceId"));

        if(filters.get("projectId") != null){
            spec = spec.and(TaskSpecifications.hasProjectId(filters.get("projectId")));
        }

        if(filters.get("assigneeId") != null){
            spec = spec.and(TaskSpecifications.hasAssigneeId(filters.get("assigneeId")));
        }

        if(filters.get("status") != null){
            spec = spec.and(TaskSpecifications.hasStatus(Status.valueOf(filters.get("status"))));
        }

        if(filters.get("dueDate") != null){
            spec = spec.and(TaskSpecifications.hasDueDate(LocalDateTime.parse(filters.get("dueDate"))));
        }

        if(filters.get("search") != null){
            spec = spec.and(TaskSpecifications.containsName(filters.get("search")));
        }

        spec = spec.and(TaskSpecifications.orderBy("createdAt", false));

        List<Task> tasks = taskRepository.findAll(spec);

        List<String> projectIds = tasks.stream().map(Task::getProjectId).toList();
        List<String> assigneeIds = tasks.stream().map(Task::getAssigneeId).toList();

        List<Project> projects = projectRepository.findAllById(projectIds);
        List<Member> members = memberRepository.findAllById(assigneeIds);

        List<UserMemberDetailsDto> assignees = members.stream().map(value -> {

            User user = userRepository.findById(value.getUserId()).orElseThrow(() ->
                    new IllegalArgumentException("User not found with id: "+value.getUserId()));

            return UserMemberDetailsDto.builder()
                    .memberId(value.getId())
                    .workspaceId(value.getWorkspaceId())
                    .userId(value.getUserId())
                    .role(value.getRole())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .build();

        }).toList();

        return tasks.stream().map(task -> {

            Project project = projects.stream().filter(p ->
                    p.getId().equals(task.getProjectId())).toList().getFirst();

            UserMemberDetailsDto assignee = assignees.stream().filter(a ->
                    a.getMemberId().equals(task.getAssigneeId())).toList().getFirst();


            return TasksProjectAssigneeDetailsDto.builder().task(task).project(project).assignee(assignee).build();

        }).toList();
    }

    public TasksProjectAssigneeDetailsDto getTask(String id, String userId) {

        Task task = taskRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Task not found with Id: "+id));

        Member currentMember = memberRepository.findByUserIdAndWorkspaceId(userId, task.getWorkspaceId());

        if(currentMember == null){
            throw new IllegalArgumentException("Unauthorized");
        }

        Project project = projectRepository.findById(task.getProjectId()).orElseThrow(() ->
                new IllegalArgumentException("Project not found with Id: "+task.getProjectId()));

        Member member = memberRepository.findById(task.getAssigneeId()).orElseThrow(() ->
                new IllegalArgumentException("Member not found with Id: "+task.getAssigneeId()));

        User user = userRepository.findById(member.getUserId()).orElseThrow(() ->
                new IllegalArgumentException("User not found with Id: "+member.getUserId()));

        UserMemberDetailsDto assignee = UserMemberDetailsDto.builder()
                .memberId(member.getId())
                .workspaceId(member.getWorkspaceId())
                .role(member.getRole())
                .userId(member.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();

        return TasksProjectAssigneeDetailsDto.builder()
                .assignee(assignee)
                .project(project)
                .task(task)
                .build();
    }

    public Task createTask(String userId, @Valid AddTaskDto addTaskDto) {

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, addTaskDto.getWorkspaceId());

        if(member == null){
            throw new IllegalArgumentException("Unauthorized");
        }

        Task highestPositionTask = taskRepository.findFirstByWorkspaceIdAndStatusOrderByPositionAsc(addTaskDto.getWorkspaceId(), addTaskDto.getStatus());

        int newPosition =
                highestPositionTask != null
                        ? highestPositionTask.getPosition() + 1000
                        : 1000;

//        Task newTask = modelMapper.map(addTaskDto, Task.class);
//        newTask.setPosition(newPosition);
//        newTask.setId(null);

        Task newTask = Task.builder()
                .workspaceId(addTaskDto.getWorkspaceId())
                .projectId(addTaskDto.getProjectId())
                .assigneeId(addTaskDto.getAssigneeId())
                .status(addTaskDto.getStatus())
                .description(addTaskDto.getDescription())
                .name(addTaskDto.getName())
                .dueDate(addTaskDto.getDueDate())
                .position(newPosition)
                .id(null)
                .build();

        return taskRepository.save(newTask);
    }

    public Task updateTask(String id, String userId, Map<String, Object> updates) {

        Task task = taskRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Task not found with Id: "+id));

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, task.getWorkspaceId());

        if(member == null){
            throw new IllegalArgumentException("Unauthorized");
        }

        updates.forEach((field, value) -> {
            switch (field){
                case "name":
                    task.setName((String) value);
                    break;

                case "projectId":
                    task.setProjectId((String) value);
                    break;

                case "assigneeId":
                    task.setAssigneeId((String) value);
                    break;

                case "description":
                    task.setDescription((String) value);
                    break;

                case "dueDate":
                    task.setDueDate((LocalDateTime) value);
                    break;

                case "status":
                    task.setStatus(Status.valueOf((String) value));
                    break;

                default:
                    throw new IllegalArgumentException("Field is not supported");
            }
        });

        return taskRepository.save(task);
    }

    public void deleteTask(String id, String userId) {

        Task task = taskRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Task not found with Id: "+id));

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, task.getWorkspaceId());

        if(member == null){
            throw new IllegalArgumentException("Unauthorized");
        }

        taskRepository.deleteById(id);
    }

    public List<Task> bulkUpdateTasks(String userId, List<BulkUpdateTasksDto> updates) {

        List<String> taskIds = updates.stream().map(BulkUpdateTasksDto::getId).toList();

        List<Task> tasksToUpdate = taskRepository.findAllById(taskIds);


        Set<String> workspaceIds = new HashSet<>();
        tasksToUpdate.forEach(task -> workspaceIds.add(task.getWorkspaceId()));

        if(workspaceIds.size() != 1){
            throw new IllegalArgumentException("All tasks must belong to the same workspace.");
        }

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, workspaceIds.iterator().next());

        if(member == null){
            throw new IllegalArgumentException("Unauthorized");
        }

        tasksToUpdate = tasksToUpdate.stream().peek(task -> {

            BulkUpdateTasksDto updatedTask = updates.stream().filter(u ->
                    u.getId().equals(task.getId())).toList().getFirst();

            task.setPosition(updatedTask.getPosition());
            task.setStatus(updatedTask.getStatus());

        }).toList();


        return taskRepository.saveAll(tasksToUpdate);

    }
}
