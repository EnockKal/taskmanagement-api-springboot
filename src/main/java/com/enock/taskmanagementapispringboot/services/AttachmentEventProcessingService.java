package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.AttachmentUploadedEvent;
import com.enock.taskmanagementapispringboot.entities.ProcessedEvent;
import com.enock.taskmanagementapispringboot.repository.ProcessedEventRepository;
import com.enock.taskmanagementapispringboot.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttachmentEventProcessingService {
    private final ProcessedEventRepository processedEventRepository;
    private final TaskRepository taskRepository;

    public AttachmentEventProcessingService(
            ProcessedEventRepository processedEventRepository,
            TaskRepository taskRepository){
        this.processedEventRepository = processedEventRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public void processEvent(AttachmentUploadedEvent event) {
        String eventId = event.getEventId();
        if (eventId == null || eventId.isBlank())
            throw new IllegalArgumentException("Failed to process attachment: eventId cannot be null or empty.");

        if (processedEventRepository.existsById(eventId)){
            return;
        }

        processedEventRepository.save(new ProcessedEvent(eventId));
    }
}
