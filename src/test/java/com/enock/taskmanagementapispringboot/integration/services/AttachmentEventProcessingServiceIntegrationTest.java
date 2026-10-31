package com.enock.taskmanagementapispringboot.integration.services;

import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.AttachmentUploadedEvent;
import com.enock.taskmanagementapispringboot.entities.TaskAttachment;
import com.enock.taskmanagementapispringboot.enums.AttachmentProcessingStatus;
import com.enock.taskmanagementapispringboot.exceptions.ResourceNotFoundException;
import com.enock.taskmanagementapispringboot.repository.ProcessedEventRepository;
import com.enock.taskmanagementapispringboot.repository.TaskAttachmentRepository;
import com.enock.taskmanagementapispringboot.services.AttachmentEventProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class AttachmentEventProcessingServiceIntegrationTest {
    @MockitoBean
    private SqsClient sqsClient;

    @Autowired
    private AttachmentEventProcessingService attachmentEventProcessingService;

    @Autowired
    private TaskAttachmentRepository taskAttachmentRepository;

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @Test
    void shouldProcessAttachmentEventSuccessfully(){
        TaskAttachment attachment = new TaskAttachment();
        attachment.setProcessingStatus(AttachmentProcessingStatus.PENDING);

        TaskAttachment savedAttachment = taskAttachmentRepository.save(attachment);

        AttachmentUploadedEvent event = new AttachmentUploadedEvent(
                savedAttachment.getId(),
                "ATTACHMENT_UPLOADED",
                "test-file.png",
                "test-file.png",
                6L,
                UUID.randomUUID().toString()
        );

        attachmentEventProcessingService.processEvent(event);

        TaskAttachment updatedAttachment = taskAttachmentRepository.findById(savedAttachment.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Not found"));

        assertEquals(AttachmentProcessingStatus.PROCESSED, updatedAttachment.getProcessingStatus());
    }

}
