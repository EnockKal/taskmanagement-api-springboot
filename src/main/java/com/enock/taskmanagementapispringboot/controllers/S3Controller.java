package com.enock.taskmanagementapispringboot.controllers;

import com.enock.taskmanagementapispringboot.dtos.S3DTO.S3FileResponse;
import com.enock.taskmanagementapispringboot.services.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {
    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<S3FileResponse> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        S3FileResponse map = s3Service.uploadFile(file);

        return ResponseEntity.ok(map);
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<String> presignedUrl(@RequestParam String fileName) {
        return ResponseEntity.ok(s3Service.presignedUrl(fileName));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam String fileName) {
        String response = s3Service.deleteFile(fileName);

        return ResponseEntity.ok(response);
    }
}
