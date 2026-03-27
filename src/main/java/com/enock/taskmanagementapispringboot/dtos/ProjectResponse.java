package com.enock.taskmanagementapispringboot.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Long projectId;
    private String projectName;
    private String projectDescription;
}
