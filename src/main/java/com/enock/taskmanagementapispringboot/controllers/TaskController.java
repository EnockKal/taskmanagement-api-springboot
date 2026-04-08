package com.enock.taskmanagementapispringboot.controllers;

import com.enock.taskmanagementapispringboot.dtos.taskDTO.TaskRequest;
import com.enock.taskmanagementapispringboot.dtos.taskDTO.TaskResponse;
import com.enock.taskmanagementapispringboot.dtos.taskDTO.TaskUpdateRequest;
import com.enock.taskmanagementapispringboot.enums.TaskStatus;
import com.enock.taskmanagementapispringboot.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Tasks")
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Create a new task",
            description = "Creates a new task associated with a project. Returns the created task with generated ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public TaskResponse createTask(@Valid @RequestBody TaskRequest taskRequest){
        return taskService.createTask(taskRequest);
    }

    @GetMapping
    @Operation(summary = "Get all tasks",
            description = "Retrieves a paginated list of tasks with optional filtering by status, project ID, " +
                    "and title. Supports pagination, sorting, and filtering."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
    })
    public Page<TaskResponse> getAllTasks(@RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "3") int size,
                                          @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
                                          @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                          @RequestParam(required = false, value = "taskStatus") TaskStatus taskStatus,
                                          @RequestParam(required = false, name = "projectId") Long projectId,
                                          @RequestParam(required = false, name = "title") String title
    ) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase()); // Convert to uppercase
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return taskService.getAllTasks(projectId, pageable, taskStatus, title);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID",
            description = "Retrieves a task by their unique ID. Returns 404 if the task is not found."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public TaskResponse getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task by ID",
            description = "Updates task details by ID. Only provided fields are updated. Returns the updated task."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public TaskResponse updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest taskUpdateRequest){
        return taskService.updateTask(id, taskUpdateRequest);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task by ID",
            description = "Deletes a task by ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public String deleteTaskById(@PathVariable Long id){
        return taskService.deleteTaskById(id);
    }
}
