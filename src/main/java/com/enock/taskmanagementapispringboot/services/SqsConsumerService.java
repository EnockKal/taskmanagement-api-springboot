package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.AttachmentUploadedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Slf4j
@Service
public class SqsConsumerService {

    private final String queueUrl;
    private final SqsClient sqsClient;
    private final JsonMapper jsonMapper;
    private final AttachmentEventProcessingService attachmentEventProcessingService;


    public SqsConsumerService(
            @Value("${aws.sqs.url}")String queueUrl,
            SqsClient sqsClient,
            JsonMapper jsonMapper,
            AttachmentEventProcessingService attachmentEventProcessingService) {
        this.queueUrl = queueUrl;
        this.sqsClient = sqsClient;
        this.jsonMapper = jsonMapper;
        this.attachmentEventProcessingService = attachmentEventProcessingService;
    }

    public void pollMessages(){
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .waitTimeSeconds(20)
                .build();

        ReceiveMessageResponse response = sqsClient.receiveMessage(receiveMessageRequest);

        List<Message> messages = response.messages();

        for (Message message : messages){
            log.info("Message ID: {}", message.messageId());
            log.info("Message Body: {}", message.body());

            try {
                AttachmentUploadedEvent event = jsonMapper.readValue(message.body(), AttachmentUploadedEvent.class);

                attachmentEventProcessingService.processEvent(event);

                log.info("TaskId: {}", event.getTaskId());
                log.info("EventId: {}", event.getEventId());
                log.info("AttachmentId: {}", event.getAttachmentId());
                log.info("EventType: {}", event.getEventType());

                DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build();

                sqsClient.deleteMessage(deleteMessageRequest);

            } catch (Exception ex){
                log.error("Failed to process SQS message ID: {}. Reason: {}", message.messageId(), ex.getMessage(), ex);
            }
        }

    }
}
