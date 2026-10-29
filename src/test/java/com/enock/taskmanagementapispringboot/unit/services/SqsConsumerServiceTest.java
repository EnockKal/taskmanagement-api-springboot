package com.enock.taskmanagementapispringboot.unit.services;

import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.AttachmentUploadedEvent;
import com.enock.taskmanagementapispringboot.services.AttachmentEventProcessingService;
import com.enock.taskmanagementapispringboot.services.SqsConsumerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SqsConsumerServiceTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private JsonMapper jsonMapper;

    @Mock
    private AttachmentEventProcessingService attachmentEventProcessingService;

    private SqsConsumerService sqsConsumerService;

    @BeforeEach
    void setUp(){
        String queueUrl = "https://sqs.us-east-2.amazonaws.com/123456789012/test-queue";
        sqsConsumerService = new SqsConsumerService(queueUrl, sqsClient, jsonMapper, attachmentEventProcessingService);
    }

    @Test
    void shouldDeleteMessageAfterSuccessfulProcessing() throws Exception {
        Message message = Message.builder()
                .messageId("msg-123")
                .receiptHandle("receipt-123")
                .body("{\"taskId\":6,\"attachmentId\":27,\"eventType\":\"ATTACHMENT_UPLOADED\"}")
                .build();

        ReceiveMessageResponse response = ReceiveMessageResponse.builder()
                .messages(message)
                .build();

        AttachmentUploadedEvent event = new AttachmentUploadedEvent(
                27L,
                "ATTACHMENT_UPLOADED",
                "test-file.png",
                "test-file.png",
                6L,
                "evt-test-123");

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(response);
        when(jsonMapper.readValue(message.body(), AttachmentUploadedEvent.class)).thenReturn(event);

        sqsConsumerService.pollMessages();

        verify(sqsClient, times(1)).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void shouldNotDeleteMessageWhenProcessingFails() throws Exception {
        Message message = Message.builder()
                .messageId("msg-123")
                .receiptHandle("recipe-123")
                .body("THIS_IS_NOT_VALID_JSON")
                .build();

        ReceiveMessageResponse response = ReceiveMessageResponse.builder()
                .messages(message)
                .build();

        AttachmentUploadedEvent event = new AttachmentUploadedEvent(
                27L,
                "ATTACHMENT_UPLOADED",
                "test-file.png",
                "test-file.png",
                6L,
                "evt-123"
        );

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(response);
        when(jsonMapper.readValue(message.body(), AttachmentUploadedEvent.class)).thenReturn(event);

        doThrow(new RuntimeException("Database failure")).when(attachmentEventProcessingService).processEvent(event);

        sqsConsumerService.pollMessages();

        verify(attachmentEventProcessingService, times(1)).processEvent(event);
        verify(sqsClient, never()).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void shouldContinueProcessingAfterOneMessageFails() throws Exception {
        Message message1 = Message.builder()
                .messageId("msg-101")
                .receiptHandle("recipe-101")
                .body("{\"taskId\":6,\"attachmentId\":27,\"eventType\":\"ATTACHMENT_UPLOADED\"}")
                .build();

        Message message2 = Message.builder()
                .messageId("msg-102")
                .receiptHandle("recipe-102")
                .body("THIS_IS_NOT_VALID_JSON")
                .build();

        Message message3 = Message.builder()
                .messageId("msg-103")
                .receiptHandle("recipe-103")
                .body("{\"taskId\":6,\"attachmentId\":28,\"eventType\":\"ATTACHMENT_UPLOADED\"}")
                .build();

        ReceiveMessageResponse response = ReceiveMessageResponse.builder()
                .messages(message1, message2, message3)
                .build();

        AttachmentUploadedEvent event1 = new AttachmentUploadedEvent(
                27L,
                "ATTACHMENT_UPLOADED",
                "test-file.png",
                "test-file.png",
                6L,
                "evt-test-123"
        );
        AttachmentUploadedEvent event3 = new AttachmentUploadedEvent(
                28L,
                "ATTACHMENT_UPLOADED",
                "test-file.png",
                "test-file.png",
                6L,
                "evt-test-123"
        );

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(response);
        when(jsonMapper.readValue(message1.body(), AttachmentUploadedEvent.class)).thenReturn(event1);
        when(jsonMapper.readValue(message2.body(), AttachmentUploadedEvent.class)).thenThrow(
                new IllegalArgumentException("Invalid JSON"));
        when(jsonMapper.readValue(message3.body(), AttachmentUploadedEvent.class)).thenReturn(event3);

        sqsConsumerService.pollMessages();

        ArgumentCaptor<DeleteMessageRequest> captor = ArgumentCaptor.forClass(DeleteMessageRequest.class);

        verify(sqsClient, times(2)).deleteMessage(captor.capture());

        List<DeleteMessageRequest> deleteMessageRequestList = captor.getAllValues();

        List<String> deletedReceipts = deleteMessageRequestList.stream()
                .map(DeleteMessageRequest::receiptHandle)
                .toList();

        assertTrue(deletedReceipts.contains("recipe-101"));
        assertTrue(deletedReceipts.contains("recipe-103"));
        assertFalse(deletedReceipts.contains("recipe-102"));
    }

    @Test
    void shouldNotDeleteWhenQueueIsEmpty() {
        ReceiveMessageResponse response = ReceiveMessageResponse.builder()
                .messages(List.of())
                .build();

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(response);

        sqsConsumerService.pollMessages();

        verify(sqsClient, never()).deleteMessage(any(DeleteMessageRequest.class));
        verifyNoInteractions(jsonMapper);
        // or
        // verify(jsonMapper, never()).readValue(any(String.class), eq(AttachmentUploadedEvent.class));
    }

    @Test
    void shouldPropagateExceptionWhenSqsReceiveFails() {
        SqsException sqsException = (SqsException) SqsException.builder()
                        .message("SQS unavailable")
                                        .build();
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenThrow(sqsException);

        SqsException exception = assertThrows(SqsException.class, () -> sqsConsumerService.pollMessages());

        assertEquals("SQS unavailable", exception.getMessage());
        verify(sqsClient, never()).deleteMessage(any(DeleteMessageRequest.class));
    }
}
