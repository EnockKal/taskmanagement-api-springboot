package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.S3DTO.S3FileResponse;
import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.TaskAttachmentResponse;
import com.enock.taskmanagementapispringboot.entities.Task;
import com.enock.taskmanagementapispringboot.entities.TaskAttachment;
import com.enock.taskmanagementapispringboot.exceptions.ResourceNotFoundException;
import com.enock.taskmanagementapispringboot.mappers.TaskAttachmentMapper;
import com.enock.taskmanagementapispringboot.repository.TaskAttachmentRepository;
import com.enock.taskmanagementapispringboot.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskAttachmentService {
    private final TaskAttachmentRepository taskAttachmentRepository;
    private final TaskRepository taskRepository;
    private final TaskAttachmentMapper taskAttachmentMapper;
    private final S3Service s3Service;

    public TaskAttachmentService(TaskAttachmentRepository taskAttachmentRepository, TaskRepository taskRepository, TaskAttachmentMapper taskAttachmentMapper, S3Service s3Service, S3Mapper s3Mapper, S3Service s3Service1) {
        this.taskAttachmentRepository = taskAttachmentRepository;
        this.taskRepository = taskRepository;
        this.taskAttachmentMapper = taskAttachmentMapper;
        this.s3Service = s3Service1;
    }

    public List<TaskAttachmentResponse> findByTaskId(Long taskId) {
        taskRepository.findById(taskId).orElseThrow(() ->
                new ResourceNotFoundException("Task with id: " + taskId + " Not Found"));

        List<TaskAttachment> taskAttachments = taskAttachmentRepository.findByTaskId(taskId);

        return taskAttachmentMapper.mapToTaskAttachmentList(taskAttachments);
    }

    public TaskAttachmentResponse uploadFile(Long taskId, MultipartFile file) throws IOException {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new ResourceNotFoundException("Task with id: " + taskId + " Not Found"));

        S3FileResponse service = s3Service.uploadFile(file);

        TaskAttachment taskAttachment = new TaskAttachment();

        taskAttachment.setOriginalFileName(file.getOriginalFilename());
        taskAttachment.setObjectKey(file.getOriginalFilename());
        taskAttachment.setFileSize(file.getSize());
        taskAttachment.setUploadedAt(LocalDateTime.now());
        taskAttachment.setTask(task);

        TaskAttachment savedTaskAttachment = taskAttachmentRepository.save(taskAttachment);

        return taskAttachmentMapper.mapTaskAttachmentToTaskAttachmentResponse(savedTaskAttachment);

    }
}
