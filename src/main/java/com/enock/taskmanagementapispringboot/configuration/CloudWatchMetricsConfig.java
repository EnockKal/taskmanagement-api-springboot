package com.enock.taskmanagementapispringboot.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;

@Configuration
public class CloudWatchMetricsConfig {
    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public CloudWatchClient cloudWatchClient(StaticCredentialsProvider credentialsProvider){
        return CloudWatchClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
