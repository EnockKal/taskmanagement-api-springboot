package com.enock.taskmanagementapispringboot.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;

@Configuration
@Profile("!test")
public class CloudWatchLogsConfig {
    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public CloudWatchLogsClient cloudWatchLogsClient(StaticCredentialsProvider credentialsProvider) {
        return CloudWatchLogsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
