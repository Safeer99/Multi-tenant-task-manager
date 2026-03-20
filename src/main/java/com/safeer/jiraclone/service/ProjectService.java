package com.safeer.jiraclone.service;

import com.safeer.jiraclone.dto.AddProjectDto;
import com.safeer.jiraclone.entity.Member;
import com.safeer.jiraclone.entity.Project;
import com.safeer.jiraclone.entity.Role;
import com.safeer.jiraclone.repository.MemberRepository;
import com.safeer.jiraclone.repository.ProjectRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Project> getAllProjects(String userId, Map<String, String> data) {

        String workspaceId = data.get("workspaceId");

        if(workspaceId == null){
            throw new IllegalArgumentException("WorkspaceId not found!");
        }

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, workspaceId);

        if(member == null){
            throw new IllegalArgumentException("Unauthorized");
        }

        return projectRepository.findAllByWorkspaceId(workspaceId);
    }

    public Project getProjectById(String id, String userId) {

        Project project = projectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project not found with ID: "+ id));

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, project.getWorkspaceId());

        if(member == null){
            throw new IllegalArgumentException("Unauthorized");
        }

        return project;
    }

    public Project createProject(String userId, AddProjectDto addProjectDto) {

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, addProjectDto.getWorkspaceId());

        if(member == null){
            throw new IllegalArgumentException("Unauthorized");
        }

        Project newProject = modelMapper.map(addProjectDto, Project.class);

        newProject.setId(null);

        return projectRepository.save(newProject);
    }

    public Project updateProject(String id, String userId, Map<String, Object> updates) {

        Project project = projectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project not found with ID: "+ id));

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, project.getWorkspaceId());

        if(member == null){
            throw new IllegalArgumentException("Unauthorized");
        }

        updates.forEach((field, value) -> {
            switch (field){
                case "name":
                    project.setName((String) value);
                    break;

                default:
                    throw new IllegalArgumentException("Field is not supported");
            }
        });

        return projectRepository.save(project);
    }

    public void deleteProject(String id, String userId) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project not found with ID: "+ id));

        Member member = memberRepository.findByUserIdAndWorkspaceId(userId, project.getWorkspaceId());

        if(member == null || member.getRole() == Role.MEMBER){
            throw new IllegalArgumentException("Unauthorized");
        }

//        TODO: delete tasks

        projectRepository.deleteById(id);
    }
}
