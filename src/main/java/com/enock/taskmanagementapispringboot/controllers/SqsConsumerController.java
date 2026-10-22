package com.enock.taskmanagementapispringboot.controllers;

import com.enock.taskmanagementapispringboot.services.SqsConsumerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sqs")
public class SqsConsumerController {
    private final SqsConsumerService sqsConsumerService;

    public SqsConsumerController(SqsConsumerService sqsConsumerService) {
        this.sqsConsumerService = sqsConsumerService;
    }

    @PostMapping("/poll")
    public String pollMessages(){
        sqsConsumerService.pollMessages();
        return "polling Complete";
    }
}
