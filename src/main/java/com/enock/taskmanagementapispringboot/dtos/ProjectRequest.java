package com.enock.taskmanagementapispringboot.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {
    @NotBlank(message = "Project name is required")
    private String projectName;
    @NotBlank
    @Size(min = 5, max = 100, message = "Description must be between 5 to 100 characters long.")
    private String projectDescription;
}
