package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.entities.Project;
import com.enock.taskmanagementapispringboot.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository){
        this.projectRepository = projectRepository;
    }

    public List<Project> getProjects() {
        return projectRepository.findAll();
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project " + id + " Not Found"));
    }

    public Project updateProject(Long id, Project project) {
        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project " + id + " Not Found"));

        existingProject.setProjectName(project.getProjectName());
        existingProject.setProjectDescription(project.getProjectDescription());
        return projectRepository.save(existingProject);
    }

    public String deleteProject(Long id) {
        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Project " + id + " Not Found"));

        projectRepository.deleteById(id);
        return "project " + id + ": " + existingProject.getProjectName() + " has been deleted";
    }
}
