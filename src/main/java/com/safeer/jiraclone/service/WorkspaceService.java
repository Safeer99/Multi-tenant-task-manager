package com.safeer.jiraclone.service;

import com.safeer.jiraclone.dto.AddWorkspaceDto;
import com.safeer.jiraclone.entity.*;
import com.safeer.jiraclone.repository.MemberRepository;
import com.safeer.jiraclone.repository.ProjectRepository;
import com.safeer.jiraclone.repository.TaskRepository;
import com.safeer.jiraclone.repository.WorkspaceRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class WorkspaceService {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Workspace> getAllWorkspaces(String userId) {
        List<Member> membersList = memberRepository.findAllByUserId(userId);

        if(membersList.toArray().length == 0){
            return List.of();
        }

        List<String> workspaceIds = membersList.stream().map(Member::getWorkspaceId).toList();

        return workspaceRepository.findAllById(workspaceIds);
    }

    public Workspace getWorkspaceById(String id, String userId){
        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, id);

        if(member == null){
            throw new IllegalArgumentException("Unauthorized");
        }

        return workspaceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Workspace not found with ID: " + id));
    }

    public Workspace createWorkspace(AddWorkspaceDto addWorkspaceDto, String userId) {

        Workspace newWorkspace = modelMapper.map(addWorkspaceDto, Workspace.class);

        newWorkspace.setUserId(userId);
        newWorkspace.setInviteCode(generateInviteCode());

        Workspace workspace = workspaceRepository.save(newWorkspace);

        Member newMember = new Member();
        newMember.setUserId(userId);
        newMember.setWorkspaceId(workspace.getId());
        newMember.setRole(Role.ADMIN);

        memberRepository.save(newMember);

        return workspace;
    }

    public Workspace updateWorkspace(String id, String userId, Map<String, Object> updates) {

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, id);

        if(member == null || member.getRole() != Role.ADMIN){
            throw new IllegalArgumentException("Unauthorized");
        }

        Workspace workspace = workspaceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Workspace not found with ID: " + id));

        updates.forEach((field, value) -> {
            switch (field){
                case "name":
                    workspace.setName((String) value);
                    break;

                default:
                    throw new IllegalArgumentException("Field is not supported");
            }
        });

        return workspaceRepository.save(workspace);
    }

    public void deleteWorkspace(String id, String userId) {

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, id);

        if(member == null || member.getRole() != Role.ADMIN){
            throw new IllegalArgumentException("Unauthorized");
        }

//        TODO: delete members, projects and tasks

        List<Member> members = memberRepository.findAllByWorkspaceId(id);
        List<Project> projects = projectRepository.findAllByWorkspaceId(id);
        List<Task> tasks = taskRepository.findAllByWorkspaceId(id);

        List<String> membersIds = members.stream().map(Member::getId).toList();
        List<String> projectsIds = projects.stream().map(Project::getId).toList();
        List<String> tasksIds = tasks.stream().map(Task::getId).toList();

        taskRepository.deleteAllById(tasksIds);
        projectRepository.deleteAllById(projectsIds);
        memberRepository.deleteAllById(membersIds);

        workspaceRepository.deleteById(id);
    }

    public Workspace resetInviteCode(String id, String userId) {

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, id);

        if(member == null || member.getRole() != Role.ADMIN){
            throw new IllegalArgumentException("Unauthorized");
        }

        Workspace workspace = workspaceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Workspace not found with ID: " + id));

        workspace.setInviteCode(generateInviteCode());

        return workspaceRepository.save(workspace);
    }

    public Workspace joinWorkspace(String id, String userId, @Valid int inviteCode) {

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, id);

        if(member != null){
            throw new IllegalArgumentException("Already a member");
        }

        Workspace workspace = workspaceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Workspace not found with ID: " + id));

        if(workspace.getInviteCode() != inviteCode){
            throw new IllegalArgumentException("Invalid invite code");
        }

        Member newMember = new Member();
        newMember.setWorkspaceId(id);
        newMember.setUserId(userId);
        newMember.setRole(Role.MEMBER);

        memberRepository.save(newMember);

        return workspace;
    }

    private int generateInviteCode(){
        Random random = new Random();

        return 100000 + random.nextInt(900000);
    }

}
