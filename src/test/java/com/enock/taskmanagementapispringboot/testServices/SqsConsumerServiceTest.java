package com.enock.taskmanagementapispringboot.testServices;

import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.AttachmentUploadedEvent;
import com.enock.taskmanagementapispringboot.services.SqsConsumerService;
import com.fasterxml.jackson.core.JsonParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.core.exc.StreamReadException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SqsConsumerServiceTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private JsonMapper jsonMapper;

    @InjectMocks
    private SqsConsumerService sqsConsumerService;

    @BeforeEach
    void setUp(){
        String queueUrl = "https://sqs.us-east-2.amazonaws.com/123456789012/test-queue";
        sqsConsumerService = new SqsConsumerService(queueUrl, sqsClient, jsonMapper);
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
                6L);

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(response);
        when(jsonMapper.readValue(message.body(), AttachmentUploadedEvent.class)).thenReturn(event);

        sqsConsumerService.pollMessages();

        verify(sqsClient, times(1)).deleteMessage(any(DeleteMessageRequest.class));
    }

    @Test
    void shouldNotDeleteMessageWhenProcessingFails() throws Exception {
        Message message = Message.builder()
                .messageId("msg-123")
                .receiptHandle("receip-123")
                .body("THIS_IS_NOT_VALID_JSON")
                .build();

        ReceiveMessageResponse response = ReceiveMessageResponse.builder()
                .messages(message)
                .build();

        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(response);
        when(jsonMapper.readValue(message.body(), AttachmentUploadedEvent.class))
                .thenThrow(new IllegalArgumentException("Invalid JSON"));

        sqsConsumerService.pollMessages();

        verify(sqsClient, never()).deleteMessage(any(DeleteMessageRequest.class));
    }
}
