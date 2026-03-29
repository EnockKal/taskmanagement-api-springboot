package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.taskDTO.TaskRequest;
import com.enock.taskmanagementapispringboot.dtos.taskDTO.TaskResponse;
import com.enock.taskmanagementapispringboot.dtos.taskDTO.TaskUpdateRequest;
import com.enock.taskmanagementapispringboot.entities.Project;
import com.enock.taskmanagementapispringboot.entities.Task;
import com.enock.taskmanagementapispringboot.enums.TaskStatus;
import com.enock.taskmanagementapispringboot.exceptions.ResourceNotFoundException;
import com.enock.taskmanagementapispringboot.mappers.TaskMapper;
import com.enock.taskmanagementapispringboot.repository.ProjectRepository;
import com.enock.taskmanagementapispringboot.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    private final ProjectRepository projectRepository;

    private  final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.taskMapper = taskMapper;
    }

    public TaskResponse createTask(TaskRequest taskRequest) {
        Task task = taskMapper.mapToTask(taskRequest);
        Project project = projectRepository.findById(taskRequest.getProjectId()).orElseThrow(() ->
                new ResourceNotFoundException("Project with id " + taskRequest.getProjectId() + " Not Found"));
        task.setProject(project);

        if (task.getTaskStatus() == null) task.setTaskStatus(TaskStatus.PENDING);

        Task savedTask = taskRepository.save(task);

        return taskMapper.mapToTaskResponse(savedTask);
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task " + id + " Not Found"));

        return  taskMapper.mapToTaskResponse(task);
    }

    public Page<TaskResponse> getAllTasks(Pageable pageable, TaskStatus taskStatus) {
        if (taskStatus != null) {
            Page<Task> taskPage = taskRepository.findByTaskStatus(taskStatus, pageable);
            return taskPage.map(taskMapper::mapToTaskResponse);
        }else {
            Page<Task> page = taskRepository.findAll(pageable);
            return page.map(taskMapper::mapToTaskResponse);
        }
    }

    public TaskResponse updateTask(Long id, TaskUpdateRequest taskUpdateRequest) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task with id " + id + " not Found"));

        if (taskUpdateRequest.getTitle() != null){
            task.setTitle(taskUpdateRequest.getTitle());
        }
        if (taskUpdateRequest.getTaskDescription() != null){
            task.setTaskDescription(taskUpdateRequest.getTaskDescription());
        }
        if (taskUpdateRequest.getTaskStatus() != null) {
            task.setTaskStatus(taskUpdateRequest.getTaskStatus());
        }

        if (taskUpdateRequest.getDueDate() != null) {
            task.setDueDate(taskUpdateRequest.getDueDate());
        }

        if (taskUpdateRequest.getProjectId() != null) {
            Project project = projectRepository.findById(taskUpdateRequest.getProjectId()).orElseThrow(() ->
                    new ResourceNotFoundException("Project with id " + taskUpdateRequest.getProjectId() + " not Found"));
            task.setProject(project);
        }

        Task savedTask = taskRepository.save(task);

        return taskMapper.mapToTaskResponse(savedTask);
    }

    public String deleteTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task with id " + id + " not Found"));

        taskRepository.deleteById(id);

        return "Task with id " + id + " has been deleted";
    }
}
