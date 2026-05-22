package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.S3DTO.S3FileResponse;
import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.TaskAttachmentResponse;
import com.enock.taskmanagementapispringboot.entities.Task;
import com.enock.taskmanagementapispringboot.entities.TaskAttachment;
import com.enock.taskmanagementapispringboot.exceptions.ResourceNotFoundException;
import com.enock.taskmanagementapispringboot.mappers.TaskAttachmentMapper;
import com.enock.taskmanagementapispringboot.repository.TaskAttachmentRepository;
import com.enock.taskmanagementapispringboot.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskAttachmentService {
    private final TaskAttachmentRepository taskAttachmentRepository;
    private final TaskRepository taskRepository;
    private final TaskAttachmentMapper taskAttachmentMapper;
    private final S3Service s3Service;
    private final CloudWatchService cloudWatchService;
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAttachmentService.class);

    public TaskAttachmentService(TaskAttachmentRepository taskAttachmentRepository,
                                 TaskRepository taskRepository,
                                 TaskAttachmentMapper taskAttachmentMapper,
                                 S3Service s3Service1,
                                 CloudWatchService cloudWatchService
    ) {
        this.taskAttachmentRepository = taskAttachmentRepository;
        this.taskRepository = taskRepository;
        this.taskAttachmentMapper = taskAttachmentMapper;
        this.s3Service = s3Service1;
        this.cloudWatchService = cloudWatchService;
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

        LOGGER.info("event={} taskId={} fileName={} fileSize={} contentType={} status={}",
                "s3_upload_started",
                taskId,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType(),
                "started");

        cloudWatchService.sendLogToCloudWatch(
                String.format("event=%s taskId=%d fileName=%s fileSize=%d contentType=%s status=%s",
                        "s3_upload_started",
                        taskId,
                        file.getOriginalFilename(),
                        file.getSize(),
                        file.getContentType(),
                        "started")
        );

        S3FileResponse s3FileResponse;

        try {
            s3FileResponse = s3Service.uploadFile(file);

        } catch (Exception e) {
            LOGGER.error("event={} taskId={} fileName={} fileSize={} contentType={} status={} errorMessage={}",
                    "s3_upload_failed",
                    taskId,
                    file.getOriginalFilename(),
                    file.getSize(),
                    file.getContentType(),
                    "failed",
                    e.getMessage());

            cloudWatchService.sendLogToCloudWatch(
                    String.format("event=%s taskId=%d fileName=%s fileSize=%d contentType=%s status=%s errorMessage=%s",
                            "s3_upload_failed",
                            taskId,
                            file.getOriginalFilename(),
                            file.getSize(),
                            file.getContentType(),
                            "failed",
                            e.getMessage())
            );
            throw e;
        }

        TaskAttachment taskAttachment = new TaskAttachment();

        taskAttachment.setOriginalFileName(s3FileResponse.getOriginalFilename());
        taskAttachment.setObjectKey(s3FileResponse.getObjectKey());
        taskAttachment.setFileSize(s3FileResponse.getFileSize());
        taskAttachment.setContentType(s3FileResponse.getContentType());
        taskAttachment.setUploadedAt(LocalDateTime.now());
        taskAttachment.setTask(task);

        TaskAttachment savedTaskAttachment = taskAttachmentRepository.save(taskAttachment);

        LOGGER.info("event={} taskId={} attachmentId={} fileName={} fileSize={} contentType={} status={} objectKey={}",
                "s3_upload_success",
                taskId,
                savedTaskAttachment.getId(),
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType(),
                "success",
                s3FileResponse.getObjectKey());

        cloudWatchService.sendLogToCloudWatch(
                String.format("event=%s taskId=%d attachmentId=%d fileName=%s fileSize=%d contentType=%s status=%s objectKey=%s",
                        "s3_upload_success",
                        taskId,
                        savedTaskAttachment.getId(),
                        file.getOriginalFilename(),
                        file.getSize(),
                        file.getContentType(),
                        "success",
                        s3FileResponse.getObjectKey())
        );

        return taskAttachmentMapper.mapTaskAttachmentToTaskAttachmentResponse(savedTaskAttachment);
    }

    public String downloadFile(Long taskId, Long attachmentId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new ResourceNotFoundException("Task with id: " + taskId + " Not Found"));

        TaskAttachment taskAttachment = taskAttachmentRepository.findById(attachmentId).orElseThrow(() ->
                new ResourceNotFoundException("Attachment with id: " + attachmentId + " Not Found"));

        if (!taskAttachment.getTask().getId().equals(task.getId())) {
            throw new ResourceNotFoundException("Attachment with id: " + attachmentId + " Not Found in task with id: " + taskId);
        }

        try {
            String file = taskAttachment.getObjectKey();

            String presignedUrl = s3Service.presignedUrl(file);

            LOGGER.info("event={} taskId={} attachmentId={} objectKey={} status={}",
                    "s3_presigned_url_success",
                    taskId,
                    attachmentId,
                    taskAttachment.getObjectKey(),
                    "success");

            cloudWatchService.sendLogToCloudWatch(
                    String.format("event=%s taskId=%d attachmentId=%d objectKey=%s status=%s",
                            "s3_presigned_url_success",
                            taskId,
                            attachmentId,
                            taskAttachment.getObjectKey(),
                            "success")
            );

            return presignedUrl;
        }
        catch (S3Exception | ResourceNotFoundException e){
            LOGGER.error("event={} taskId={} attachmentId={} objectKey={} status={} errorMessage={}",
                    "s3_presigned_url_failed",
                    taskId,
                    attachmentId,
                    taskAttachment.getObjectKey(), // can use "file" (cleaner) instead of "taskAttachment.getObjectKey()".
                    "failed",
                    e.getMessage());

            cloudWatchService.sendLogToCloudWatch(
                    String.format("event=%s taskId=%d attachmentId=%d objectKey=%s status=%s errorMessage=%s",
                            "s3_presigned_url_failed",
                            taskId,
                            attachmentId,
                            taskAttachment.getObjectKey(), // can use "file" (cleaner) instead of "taskAttachment.getObjectKey()".
                            "failed",
                            e.getMessage())
            );
            throw e;
        }
    }

    public String deleteFile(Long taskId, Long attachmentId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new ResourceNotFoundException("Task with id: " + taskId + " Not Found"));

        TaskAttachment taskAttachment = taskAttachmentRepository.findById(attachmentId).orElseThrow(() ->
                new ResourceNotFoundException("Attachment with id: " + attachmentId + " Not Found"));

        if (!taskAttachment.getTask().getId().equals(task.getId())) {
            throw new ResourceNotFoundException("Attachment with id: " + attachmentId + " Not Found in task with id: " + taskId);
        }

        try {
            String file = taskAttachment.getObjectKey();

            String response = s3Service.deleteFile(file);

            taskAttachmentRepository.delete(taskAttachment);

            LOGGER.info("event={} taskId={} attachmentId={} objectKey={} status={}",
                    "s3_deletion_success",
                    taskId,
                    attachmentId,
                    taskAttachment.getObjectKey(),
                    "success");

            cloudWatchService.sendLogToCloudWatch(
                    String.format("event=%s taskId=%d attachmentId=%d objectKey=%s status=%s",
                            "s3_deletion_success",
                            taskId,
                            attachmentId,
                            taskAttachment.getObjectKey(),
                            "success")
            );

            return  response;
        }
        catch (S3Exception | ResourceNotFoundException e){
            LOGGER.error("event={} taskId={} attachmentId={} objectKey={} status={} errorMessage={}",
                    "s3_deletion_failed",
                    taskId,
                    attachmentId,
                    taskAttachment.getObjectKey(),
                    "failed",
                    e.getMessage());

            cloudWatchService.sendLogToCloudWatch(
                    String.format("event=%s taskId=%d attachmentId=%d objectKey=%s status=%s errorMessage=%s",
                            "s3_deletion_failed",
                            taskId,
                            attachmentId,
                            taskAttachment.getObjectKey(),
                            "failed",
                            e.getMessage())
            );
            throw e;
        }
    }
}
