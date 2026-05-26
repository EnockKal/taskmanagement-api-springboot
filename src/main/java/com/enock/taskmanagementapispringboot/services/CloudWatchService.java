package com.enock.taskmanagementapispringboot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
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
    private final CloudWatchClient cloudWatchClient;

    public CloudWatchService(CloudWatchLogsClient cloudWatchLogsClient, CloudWatchClient cloudWatchClient) {
        this.cloudWatchLogsClient = cloudWatchLogsClient;
        this.cloudWatchClient = cloudWatchClient;
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

    public void sendMetricToCloudWatch(String metricName, Double value, StandardUnit unit) {
        try {
            MetricDatum metricDatum = MetricDatum.builder()
                    .metricName(metricName)
                    .value(value)
                    .unit(String.valueOf(unit))
                    .build();

            PutMetricDataRequest request = PutMetricDataRequest.builder()
                    .namespace("TaskManagementAPI")
                    .metricData(metricDatum)
                    .build();

            cloudWatchClient.putMetricData(request);

        } catch (Exception e){
            System.err.println("CloudWatch metric error: " + e.getMessage());
        }
    }
}
