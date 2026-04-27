package com.enock.taskmanagementapispringboot.controllers;

import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.TaskAttachmentResponse;
import com.enock.taskmanagementapispringboot.services.TaskAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "List task attachments", description = "Retrieve all attachments associated with a given task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "attachments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TaskAttachmentResponse>> getTaskAttachments(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskAttachmentService.findByTaskId(taskId));
    }

    @PostMapping
    @Operation(summary = "Upload attachment to a task",
            description = "Upload a file and attach it to a specific task. Stores metadata in the database and file in S3.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Attachment uploaded and attached to task successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or empty attachment"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TaskAttachmentResponse> uploadFile(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        TaskAttachmentResponse taskAttachmentResponse = taskAttachmentService.uploadFile(taskId, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskAttachmentResponse);
    }

    @GetMapping("/{attachmentId}/url")
    @Operation(summary = "Generate a pre-signed URL to access a private S3 attachment",
            description = "Generate secure pre-signed URL to download the attachment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "URL generated successfully"),
            @ApiResponse(responseCode = "404", description = "Task or attachment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> downloadFile(@PathVariable Long taskId, @PathVariable Long attachmentId) {
        return ResponseEntity.ok(taskAttachmentService.downloadFile(taskId, attachmentId));
    }

    @DeleteMapping("/{attachmentId}")
    @Operation(summary = "Delete attachment from S3 + DB",
            description = "Delete attachment from S3 and remove its metadata from the database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attachment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "task or attachment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> deleteFile(@PathVariable Long taskId, @PathVariable Long attachmentId) {
        return ResponseEntity.ok(taskAttachmentService.deleteFile(taskId, attachmentId));
    }
}
