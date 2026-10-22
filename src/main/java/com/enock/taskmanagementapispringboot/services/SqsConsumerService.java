package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.AttachmentUploadedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Service
public class SqsConsumerService {
    @Value("${aws.sqs.url}")
    private String queueUrl;

    private final SqsClient sqsClient;
    private final JsonMapper jsonMapper;


    public SqsConsumerService(SqsClient sqsClient, JsonMapper jsonMapper) {
        this.sqsClient = sqsClient;
        this.jsonMapper = jsonMapper;
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
            System.out.println("Message ID: " + message.messageId());
            System.out.println("Message Body: " + message.body());
            try {
                AttachmentUploadedEvent event = jsonMapper.readValue(message.body(), AttachmentUploadedEvent.class);
                System.out.println("TaskId: " + event.getTaskId());
                System.out.println("AttachmentId: " + event.getAttachmentId());
                System.out.println("EventType: " + event.getEventType());

                DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build();

                sqsClient.deleteMessage(deleteMessageRequest);

            } catch (Exception ex){
                System.err.println("An error occurred: " + ex.getMessage());
            }
        }

    }
}
