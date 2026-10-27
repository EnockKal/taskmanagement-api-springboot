package com.enock.taskmanagementapispringboot.testServices;

import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.AttachmentUploadedEvent;
import com.enock.taskmanagementapispringboot.services.AttachmentEventProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AttachmentEventProcessingServiceTest {
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

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                attachmentEventProcessingService.processEvent(event));

        assertEquals(
                "Failed to process attachment: eventId cannot be null or empty.",
                illegalArgumentException.getMessage());

    }
}
