package com.enock.taskmanagementapispringboot.controllers;

import com.enock.taskmanagementapispringboot.dtos.projectDTO.ProjectRequest;
import com.enock.taskmanagementapispringboot.dtos.projectDTO.ProjectResponse;
import com.enock.taskmanagementapispringboot.dtos.projectDTO.ProjectUpdateRequest;
import com.enock.taskmanagementapispringboot.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Projects")
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @Operation(summary = "Create a new project",
            description = "Creates a new project. Returns the created project with generated ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Project created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ProjectResponse createProject(@Valid @RequestBody ProjectRequest project) {
        return projectService.createProject(project);
    }

    @GetMapping
    @Operation(summary = "Get all projects",
            description = "Retrieves a paginated list of projects with optional filtering by status, project ID, " +
                    "and title. Supports pagination, sorting, and filtering."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
    })
    public List<ProjectResponse> getProjects() {
        return projectService.getProjects();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a project by ID",
            description = "Retrieves a project by their unique ID. Returns 404 if the project is not found."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ProjectResponse getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a project by ID",
            description = "Updates project details by ID. Only provided fields are updated. Returns the updated project."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ProjectResponse updateProject(@PathVariable Long id, @Valid @RequestBody ProjectUpdateRequest project) {
        return projectService.updateProject(id, project);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project by ID",
            description = "Deletes a project by ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    public String deleteProject(@PathVariable Long id) {
        return projectService.deleteProject(id);
    }
}
