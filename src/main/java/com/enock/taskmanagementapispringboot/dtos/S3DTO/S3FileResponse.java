package com.enock.taskmanagementapispringboot.dtos.S3DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class S3FileResponse {
    private String objectKey;
    private String originalFilename;
    private Long fileSize;
    private String contentType;
}
