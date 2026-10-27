package com.enock.taskmanagementapispringboot.testServices;

import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.AttachmentUploadedEvent;
import com.enock.taskmanagementapispringboot.repository.ProcessedEventRepository;
import com.enock.taskmanagementapispringboot.services.AttachmentEventProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttachmentEventProcessingServiceTest {
    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private AttachmentEventProcessingService attachmentEventProcessingService;

    @Test
    void shouldRejectEventWhenEventIdIsNull() {
        AttachmentUploadedEvent event = new AttachmentUploadedEvent(
                27L,
                "ATTACHMENT_UPLOADED",
                "test-file.png",
                "test-file.png",
                6L,
                null
        );

        assertThrows(IllegalArgumentException.class, () ->
                attachmentEventProcessingService.processEvent(event));

        verifyNoInteractions(processedEventRepository);
    }

    @Test
    void shouldRejectEventWhenEventIdIsBlank() {
        AttachmentUploadedEvent event = new AttachmentUploadedEvent(
                27L,
                "ATTACHMENT_UPLOADED",
                "test-file.png",
                "test-file.png",
                6L,
                "  "
        );

        assertThrows(IllegalArgumentException.class, () ->
                attachmentEventProcessingService.processEvent(event));

        verifyNoInteractions(processedEventRepository);
    }
}
