package com.enock.taskmanagementapispringboot.dtos.taskDTO;

import com.enock.taskmanagementapispringboot.enums.TaskStatus;
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
public class TaskUpdateRequest {
    private String title;

    @Size(min = 5, max = 100, message = "description must be between 5 and 100 characters")
    private String taskDescription;
    private TaskStatus taskStatus;
    private LocalDate dueDate;
    private Long projectId;
}
