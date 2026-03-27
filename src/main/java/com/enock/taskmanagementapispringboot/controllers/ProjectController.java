package com.enock.taskmanagementapispringboot.controllers;

import com.enock.taskmanagementapispringboot.dtos.ProjectRequest;
import com.enock.taskmanagementapispringboot.dtos.ProjectResponse;
import com.enock.taskmanagementapispringboot.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectResponse> getProjects() {
        return projectService.getProjects();
    }

    @PostMapping
    public ProjectResponse createProject(@Valid @RequestBody ProjectRequest project) {
        return projectService.createProject(project);
    }

    @GetMapping("/{id}")
    public ProjectResponse getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @PutMapping("/{id}")
    public  ProjectResponse updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest project) {
        return projectService.updateProject(id, project);
    }

    @DeleteMapping("/{id}")
    public  String deleteProject(@PathVariable Long id) {
        return projectService.deleteProject(id);
    }
}
