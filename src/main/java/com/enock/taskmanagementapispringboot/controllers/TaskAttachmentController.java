package com.enock.taskmanagementapispringboot.controllers;

import com.enock.taskmanagementapispringboot.dtos.S3DTO.S3FileResponse;
import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.TaskAttachmentResponse;
import com.enock.taskmanagementapispringboot.services.S3Service;
import com.enock.taskmanagementapispringboot.services.TaskAttachmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/attachments")
@Tag(name = "Task Attachments")
public class TaskAttachmentController {
    private final TaskAttachmentService taskAttachmentService;

    public TaskAttachmentController(TaskAttachmentService taskAttachmentService) {
        this.taskAttachmentService = taskAttachmentService;
    }

    @GetMapping
    public ResponseEntity<List<TaskAttachmentResponse>> getTaskAttachments(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskAttachmentService.findByTaskId(taskId));
    }

    @PostMapping
    public ResponseEntity<TaskAttachmentResponse> uploadFile(@PathVariable Long taskId, @RequestParam("file") MultipartFile file) throws IOException {
        TaskAttachmentResponse taskAttachmentResponse = taskAttachmentService.uploadFile(taskId, file);

        return ResponseEntity.ok(taskAttachmentResponse);
    }

    @GetMapping("/{attachmentId}/url")
    public ResponseEntity<String> downloadFile(@PathVariable Long taskId, @PathVariable Long attachmentId) {
        return ResponseEntity.ok(taskAttachmentService.downloadFile(taskId, attachmentId));
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long taskId, @PathVariable Long attachmentId) {
        return ResponseEntity.ok(taskAttachmentService.deleteFile(taskId, attachmentId));
    }
}
