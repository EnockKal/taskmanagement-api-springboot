package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.taskAttachmentDTO.AttachmentUploadedEvent;
//import com.fasterxml.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatchlogs.model.StandardUnit;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

@Service
public class SqsService {
    @Value("${aws.sqs.url}")
    private String queueUrl;

    private final JsonMapper jsonMapper;
    private final SqsClient sqsClient;
    private final CloudWatchService cloudWatchService;

    public SqsService(JsonMapper jsonMapper, SqsClient sqsClient, CloudWatchService cloudWatchService) {
        this.jsonMapper = jsonMapper;
        this.sqsClient = sqsClient;
        this.cloudWatchService = cloudWatchService;
    }

    public void sendAttachmentUploadedEvent(AttachmentUploadedEvent event){
        try{
            String eventJson = jsonMapper.writeValueAsString(event);

            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(eventJson)
                    .build();

            sqsClient.sendMessage(sendMessageRequest);

            cloudWatchService.sendLogToCloudWatch(
                    String.format("event=%s taskId=%d attachmentId=%d eventType=%s status=%s",
                            "sqs_message_sent",
                            event.getTaskId(),
                            event.getAttachmentId(),
                            event.getEventType(),
                            "success")
            );

            cloudWatchService.sendMetricToCloudWatch(
                    "SqsMessageSent", 1.0 , StandardUnit.COUNT
            );

        } catch (SqsException e){
            cloudWatchService.sendLogToCloudWatch(
                    String.format("event=%s taskId=%d attachmentId=%d eventType=%s status=%s errorMessage=%s",
                            "sqs_message_failed",
                            event.getTaskId(),
                            event.getAttachmentId(),
                            event.getEventType(),
                            "failed",
                            e.getMessage()
                    )
            );

            cloudWatchService.sendMetricToCloudWatch(
                    "SqsMessageFailed", 1.0 , StandardUnit.COUNT
            );

        } catch (Exception e) {
            cloudWatchService.sendLogToCloudWatch(
                    String.format("event=%s taskId=%d attachmentId=%d eventType=%s status=%s errorMessage=%s",
                            "sqs_event_processing_failed",
                            event.getTaskId(),
                            event.getAttachmentId(),
                            event.getEventType(),
                            "failed",
                            e.getMessage()
                    )
            );
            System.err.println("Unexpected error occurred: " + e.getMessage());
        }
    }
}
