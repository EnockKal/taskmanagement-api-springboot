package com.enock.taskmanagementapispringboot.mappers;

import com.enock.taskmanagementapispringboot.dtos.projectDTO.ProjectRequest;
import com.enock.taskmanagementapispringboot.dtos.projectDTO.ProjectResponse;
import com.enock.taskmanagementapispringboot.entities.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectMapper {
    public Project mapToProject(ProjectRequest projectRequest) {
        if (projectRequest == null) {
            return null;
        }
        Project project = new Project();
        project.setProjectName(projectRequest.getProjectName());
        project.setProjectDescription(projectRequest.getProjectDescription());

        return project;
    }

    public ProjectResponse mapToProjectResponse(Project  project) {
        if (project == null) {
            return null;
        }

        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setProjectId(project.getProjectId());
        projectResponse.setProjectName(project.getProjectName());
        projectResponse.setProjectDescription(project.getProjectDescription());

        return projectResponse;
    }

    public List<ProjectResponse> mapToProjectResponseList(List<Project> projects) {
        return projects.stream()
                .map(this::mapToProjectResponse)
                .toList();
    }
}
