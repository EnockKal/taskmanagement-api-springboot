package com.enock.taskmanagementapispringboot.dtos;

import com.enock.taskmanagementapispringboot.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    @NotBlank(message = "title is required")
    private String title;

    @NotBlank
    @Size(min = 5, max = 100, message = "description must be between 5 and 100 characters")
    private String taskDescription;

    private TaskStatus taskStatus;
    private LocalDate dueDate;

    @NotNull(message = "project id is required")
    private Long projectId;
}
