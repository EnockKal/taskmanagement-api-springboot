package com.enock.taskmanagementapispringboot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {
    private final S3Client s3Client;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            originalFilename = "file";
        }
        String generatedFileName = UUID.randomUUID().toString() + "-" + originalFilename;

        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(generatedFileName)
                .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        return generatedFileName;
    }

    public void deleteFile(String file) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(file)
                .build());
    }

    public  byte[] downloadFile(String key) {
        ResponseBytes<GetObjectResponse> objectAsByte = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()
        );
        return objectAsByte.asByteArray();
    }
}
