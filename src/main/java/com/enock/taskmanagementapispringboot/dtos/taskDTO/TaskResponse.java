package com.enock.taskmanagementapispringboot.dtos.taskDTO;

import com.enock.taskmanagementapispringboot.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String taskDescription;
    private TaskStatus taskStatus;
    private LocalDate dueDate;

    private Long projectId;
}
