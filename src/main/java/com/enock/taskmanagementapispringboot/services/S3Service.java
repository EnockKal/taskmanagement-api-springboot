package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.S3DTO.S3FileResponse;
import com.enock.taskmanagementapispringboot.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3Service {
    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    public S3FileResponse uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            originalFilename = "file";
        }
        String objectKey = UUID.randomUUID().toString() + "-" + originalFilename;

        s3Client.putObject(PutObjectRequest
                        .builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        String contentType = file.getContentType();
        Long fileSize = file.getSize();

        return new S3FileResponse(objectKey, originalFilename, fileSize, contentType);
    }

    public String deleteFile(String fileName) {
        try{
            s3Client.headObject(HeadObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build()
            );
            s3Client.deleteObject(DeleteObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build()
            );
        }
        catch (S3Exception e) {
            if (e.statusCode() == 404) {
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
            else {
                throw e;
            }
        }

        return "Deleted successfully";
    }

    public String presignedUrl(String fileName) { // downloading file temporary (for 10 min)
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build()
            );
            GetObjectPresignRequest preSignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(b -> b.bucket(bucketName).key(fileName))
                    .build();

            return s3Presigner.presignGetObject(preSignRequest).url().toString();
        }
        catch (S3Exception e) {
            if (e.statusCode() == 404) {
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
            else {
                throw e;
            }
        }

    }
}
