package com.enock.taskmanagementapispringboot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

import java.util.Collections;

@Service
public class CloudWatchService {
    @Value("${aws.cloudwatch.log-group-name}")
    private String logGroupName;

    @Value("${aws.cloudwatch.log-stream-name}")
    private String logStreamName;

    private final CloudWatchLogsClient cloudWatchLogsClient;

    public CloudWatchService(CloudWatchLogsClient cloudWatchLogsClient) {
        this.cloudWatchLogsClient = cloudWatchLogsClient;
    }

    public void sendLogToCloudWatch(String message) {
        try {
            DescribeLogStreamsRequest logStreamsRequest = DescribeLogStreamsRequest.builder()
                    .logGroupName(logGroupName)
                    .logStreamNamePrefix(logStreamName)
                    .build();

            DescribeLogStreamsResponse logStreamsResponse = cloudWatchLogsClient
                    .describeLogStreams(logStreamsRequest);

            String token = logStreamsResponse.logStreams()
                    .getFirst()
                    .uploadSequenceToken();

            InputLogEvent logEvent = InputLogEvent.builder()
                    .message(message)
                    .timestamp(System.currentTimeMillis())
                    .build();

            PutLogEventsRequest logEventsRequest = PutLogEventsRequest.builder()
                    .logGroupName(logGroupName)
                    .logStreamName(logStreamName)
                    .logEvents(Collections.singletonList(logEvent))
                    .sequenceToken(token)
                    .build();

            cloudWatchLogsClient.putLogEvents(logEventsRequest);
        } catch (CloudWatchLogsException e){
            System.err.println("CloudWatchLogsException: " + e.getMessage());
        } catch (Exception e){
            System.err.println("Unexpected error occurred: " + e.getMessage());
        }
    }
}
