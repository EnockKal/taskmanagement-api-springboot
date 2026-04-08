package com.enock.taskmanagementapispringboot;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@OpenAPIDefinition(
        info = @Info(
                title = "Task Management API",
                description = "REST API for managing projects, tasks, and users with assignment capabilities",
                version = "1.0"
        )
)
public class TaskmanagementApiSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskmanagementApiSpringbootApplication.class, args);
    }

}
