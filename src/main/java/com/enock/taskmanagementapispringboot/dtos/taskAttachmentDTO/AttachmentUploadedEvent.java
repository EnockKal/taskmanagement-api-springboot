package com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AttachmentUploadedEvent {
    private Long attachmentId;
    private String eventType;
    private String objectKey;
    private String fileName;

    private Long taskId;
}
