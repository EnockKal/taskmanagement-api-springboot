package com.enock.taskmanagementapispringboot.mappers;

import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.TaskAttachmentResponse;
import com.enock.taskmanagementapispringboot.entities.TaskAttachment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskAttachmentMapper {
    public TaskAttachmentResponse mapTaskAttachmentToTaskAttachmentResponse(TaskAttachment taskAttachment) {
        if (taskAttachment == null) {
            return null;
        }

        TaskAttachmentResponse taskAttachmentResponse = new TaskAttachmentResponse();
        taskAttachmentResponse.setId(taskAttachment.getId());
        taskAttachmentResponse.setOriginalFileName(taskAttachment.getOriginalFileName());
        taskAttachmentResponse.setFileSize(taskAttachment.getFileSize());
        taskAttachmentResponse.setObjectKey(taskAttachment.getObjectKey());
        taskAttachmentResponse.setContentType(taskAttachment.getContentType());
        taskAttachmentResponse.setUploadedAt(taskAttachment.getUploadedAt());

        return taskAttachmentResponse;
    }

//    public TaskAttachment mapTaskAttachmentResponseToTaskAttachment(TaskAttachmentResponse taskAttachmentResponse) {
//        if (taskAttachmentResponse == null) {
//            return null;
//        }
//
//        TaskAttachment taskAttachment = new TaskAttachment();
//        taskAttachment.setId(taskAttachmentResponse.getId());
//        taskAttachment.setOriginalFileName(taskAttachmentResponse.getOriginalFileName());
//        taskAttachment.setFileSize(taskAttachmentResponse.getFileSize());
//        taskAttachment.setObjectKey(taskAttachmentResponse.getObjectKey());
//        taskAttachment.setContentType(taskAttachmentResponse.getContentType());
//        taskAttachment.setUploadedAt(taskAttachmentResponse.getUploadedAt());
//
//        return taskAttachment;
//    }

    public List<TaskAttachmentResponse> mapToTaskAttachmentList(List<TaskAttachment> taskAttachments) {
        return taskAttachments.stream()
                .map(this::mapTaskAttachmentToTaskAttachmentResponse)
                .toList();
    }
}
