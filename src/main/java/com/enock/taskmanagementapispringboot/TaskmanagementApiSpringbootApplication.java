package com.enock.taskmanagementapispringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.enock.taskmanagementapispringboot.entities")
public class TaskmanagementApiSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskmanagementApiSpringbootApplication.class, args);
    }

}
