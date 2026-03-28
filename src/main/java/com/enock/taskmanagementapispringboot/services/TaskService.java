package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.TaskRequest;
import com.enock.taskmanagementapispringboot.dtos.TaskResponse;
import com.enock.taskmanagementapispringboot.entities.Project;
import com.enock.taskmanagementapispringboot.entities.Task;
import com.enock.taskmanagementapispringboot.enums.TaskStatus;
import com.enock.taskmanagementapispringboot.exceptions.ResourceNotFoundException;
import com.enock.taskmanagementapispringboot.mappers.TaskMapper;
import com.enock.taskmanagementapispringboot.repository.ProjectRepository;
import com.enock.taskmanagementapispringboot.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<TaskResponse> getAllTasks() {
        return taskMapper.mapToTaskList(taskRepository.findAll());
    }

    public TaskResponse updateTask(Long id, TaskRequest taskRequest) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task with id " + id + " not Found"));

        task.setTitle(taskRequest.getTitle());
        task.setTaskDescription(taskRequest.getTaskDescription());
        if (taskRequest.getTaskStatus() != null) {
            task.setTaskStatus(taskRequest.getTaskStatus());
        }

        if (taskRequest.getDueDate() != null) {
            task.setDueDate(taskRequest.getDueDate());
        }

        if (taskRequest.getProjectId() != null) {
            Project project = projectRepository.findById(taskRequest.getProjectId()).orElseThrow(() ->
                    new ResourceNotFoundException("Project with id " + taskRequest.getProjectId() + " not Found"));
            task.setProject(project);
        }

        Task savedTask = taskRepository.save(task);

        return taskMapper.mapToTaskResponse(savedTask);
    }

    public String deleteTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task with id " + id + " Not Found"));

        taskRepository.deleteById(id);

        return "Task with id " + id + " has been deleted";
    }
}
