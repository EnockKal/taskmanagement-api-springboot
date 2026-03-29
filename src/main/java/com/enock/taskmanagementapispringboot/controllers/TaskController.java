package com.enock.taskmanagementapispringboot.controllers;

import com.enock.taskmanagementapispringboot.dtos.taskDTO.TaskRequest;
import com.enock.taskmanagementapispringboot.dtos.taskDTO.TaskResponse;
import com.enock.taskmanagementapispringboot.dtos.taskDTO.TaskUpdateRequest;
import com.enock.taskmanagementapispringboot.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public Page<TaskResponse> getAllTasks(@RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "3") int size,
                                          @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
                                          @RequestParam(value = "direction", defaultValue = "asc") Sort.Direction direction
    ) {
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return taskService.getAllTasks(pageable);
    }

    @PostMapping
    public TaskResponse createTask(@Valid @RequestBody TaskRequest taskRequest){
        return taskService.createTask(taskRequest);
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest taskUpdateRequest){
        return taskService.updateTask(id, taskUpdateRequest);
    }

    @DeleteMapping("/{id}")
    public String deleteTaskById(@PathVariable Long id){
        return taskService.deleteTaskById(id);
    }
}
