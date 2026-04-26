package com.enock.taskmanagementapispringboot.dtos.S3DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileResponse {
    String fileName;
    String fileSize;
    String url;
}
