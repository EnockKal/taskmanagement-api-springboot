package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.AttachmentUploadedEvent;
import com.enock.taskmanagementapispringboot.entities.ProcessedEvent;
import com.enock.taskmanagementapispringboot.entities.TaskAttachment;
import com.enock.taskmanagementapispringboot.enums.AttachmentProcessingStatus;
import com.enock.taskmanagementapispringboot.exceptions.ResourceNotFoundException;
import com.enock.taskmanagementapispringboot.repository.ProcessedEventRepository;
import com.enock.taskmanagementapispringboot.repository.TaskAttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttachmentEventProcessingService {
    private final ProcessedEventRepository processedEventRepository;
    private final TaskAttachmentRepository taskAttachmentRepository;

    public AttachmentEventProcessingService(
            ProcessedEventRepository processedEventRepository,
            TaskAttachmentRepository taskAttachmentRepository){
        this.processedEventRepository = processedEventRepository;
        this.taskAttachmentRepository = taskAttachmentRepository;
    }

    @Transactional
    public void processEvent(AttachmentUploadedEvent event) {
        String eventId = event.getEventId();
        if (eventId == null || eventId.isBlank())
            throw new IllegalArgumentException("Failed to process attachment: eventId cannot be null or empty.");

        if (processedEventRepository.existsById(eventId)){
            return;
        }

        Long attachmentId = event.getAttachmentId();

        TaskAttachment attachment = taskAttachmentRepository.findById(attachmentId).orElseThrow(() ->
                new ResourceNotFoundException("Attachment not found with ID: " + attachmentId));

        attachment.setProcessingStatus(AttachmentProcessingStatus.PROCESSED);
        //taskAttachmentRepository.save(attachment);   // no need dirty checking applied

        processedEventRepository.save(new ProcessedEvent(eventId));
    }
}
