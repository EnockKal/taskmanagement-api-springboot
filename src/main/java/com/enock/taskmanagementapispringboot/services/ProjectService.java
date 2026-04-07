package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.projectDTO.ProjectRequest;
import com.enock.taskmanagementapispringboot.dtos.projectDTO.ProjectResponse;
import com.enock.taskmanagementapispringboot.dtos.projectDTO.ProjectUpdateRequest;
import com.enock.taskmanagementapispringboot.entities.Project;
import com.enock.taskmanagementapispringboot.exceptions.ResourceNotFoundException;
import com.enock.taskmanagementapispringboot.mappers.ProjectMapper;
import com.enock.taskmanagementapispringboot.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectService(ProjectRepository projectRepository, ProjectMapper projectMapper){
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    public List<ProjectResponse> getProjects() {
        return projectMapper.mapToProjectResponseList(projectRepository.findAll());
    }

    public ProjectResponse createProject(ProjectRequest project) {
        Project projectToSave = projectMapper.mapToProject(project);
        Project savedProject = projectRepository.save(projectToSave);

        return projectMapper.mapToProjectResponse(savedProject);
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project " + id + " Not Found"));
        return projectMapper.mapToProjectResponse(project);
    }

    public ProjectResponse updateProject(Long id, ProjectUpdateRequest project) {
        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project " + id + " Not Found"));

        if (project.getProjectName() != null) {
            existingProject.setProjectName(project.getProjectName());
        }
        if (project.getProjectDescription() != null) {
            existingProject.setProjectDescription(project.getProjectDescription());
        }

        Project updatedProject = projectRepository.save(existingProject);

        return projectMapper.mapToProjectResponse(updatedProject);
    }

    public String deleteProject(Long id) {
        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project " + id + " Not Found"));

        projectRepository.deleteById(id);
        return "project " + id + ": " + existingProject.getProjectName() + " has been deleted";
    }
}
