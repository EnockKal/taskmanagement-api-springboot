package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.TaskAttachmentResponse;
import com.enock.taskmanagementapispringboot.entities.Task;
import com.enock.taskmanagementapispringboot.exceptions.ResourceNotFoundException;
import com.enock.taskmanagementapispringboot.repository.TaskAttachmentRepository;
import com.enock.taskmanagementapispringboot.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskAttachmentService {
    private final TaskAttachmentRepository taskAttachmentRepository;
    private final TaskRepository taskRepository;

    public TaskAttachmentService(TaskAttachmentRepository taskAttachmentRepository, TaskRepository taskRepository) {
        this.taskAttachmentRepository = taskAttachmentRepository;
        this.taskRepository = taskRepository;
    }

    public List<TaskAttachmentResponse> findByTaskId(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new ResourceNotFoundException("Task with id: " + taskId + " Not Found"));

        return taskAttachmentRepository.findByTaskId(taskId);
    }
}
