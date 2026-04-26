package com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskAttachmentResponse {
    private Long Id;
    private String originalFileName;
    private Long fileSize;
    private String objectKey;
    private String contentType;
    private LocalDateTime uploadedAt;
}
