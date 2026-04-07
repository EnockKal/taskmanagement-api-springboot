package com.enock.taskmanagementapispringboot.dtos.projectDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateRequest {
    private String projectName;
    private String projectDescription;
}
