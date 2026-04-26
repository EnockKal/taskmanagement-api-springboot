package com.enock.taskmanagementapispringboot.mappers;

import com.enock.taskmanagementapispringboot.dtos.S3DTO.S3FileResponse;
import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.TaskAttachmentResponse;
import org.springframework.stereotype.Component;

@Component
public class S3Mapper {
    public TaskAttachmentResponse mapToTaskAttachmentResponse(S3FileResponse s3FileResponse) {
        if (s3FileResponse == null) {
            return null;
        }

        TaskAttachmentResponse taskAttachmentResponse = new TaskAttachmentResponse();
        taskAttachmentResponse.setOriginalFileName(s3FileResponse.getOriginalFilename());
        taskAttachmentResponse.setFileSize(s3FileResponse.getFileSize());
        taskAttachmentResponse.setObjectKey(s3FileResponse.getObjectKey());
        taskAttachmentResponse.setContentType(s3FileResponse.getContentType());

        return taskAttachmentResponse;
    }
}
