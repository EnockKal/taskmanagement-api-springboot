package com.enock.taskmanagementapispringboot.unit.services;

import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.AttachmentUploadedEvent;
import com.enock.taskmanagementapispringboot.entities.ProcessedEvent;
import com.enock.taskmanagementapispringboot.repository.ProcessedEventRepository;
import com.enock.taskmanagementapispringboot.services.AttachmentEventProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @Test
    void shouldSkipAlreadyProcessedEvent() {
        AttachmentUploadedEvent event = new AttachmentUploadedEvent(
                27L,
                "ATTACHMENT_UPLOADED",
                "test-file.png",
                "test-file.png",
                6L,
                "evt-123"
        );

        when(processedEventRepository.existsById("evt-123")).thenReturn(true);

        attachmentEventProcessingService.processEvent(event);

        verify(processedEventRepository, times(1)).existsById("evt-123");
        verify(processedEventRepository, never()).save(any(ProcessedEvent.class));
        verifyNoMoreInteractions(processedEventRepository);
    }

    @Test
    void shouldSaveNewProcessedEvent() {
        AttachmentUploadedEvent event = new AttachmentUploadedEvent(
                27L,
                "ATTACHMENT_UPLOADED",
                "test-file.png",
                "test-file.png",
                6L,
                "evt-123"
        );

        when(processedEventRepository.existsById("evt-123")).thenReturn(false);

        attachmentEventProcessingService.processEvent(event);

        verify(processedEventRepository, times(1)).existsById("evt-123");

        ArgumentCaptor<ProcessedEvent> captor = ArgumentCaptor.forClass(ProcessedEvent.class);
        verify(processedEventRepository, times(1)).save(captor.capture());

        ProcessedEvent savedEvent = captor.getValue();

        assertEquals("evt-123", savedEvent.getEventId());
    }
}
