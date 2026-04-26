package com.enock.taskmanagementapispringboot.services;

import com.enock.taskmanagementapispringboot.dtos.S3DTO.FileResponse;
import com.enock.taskmanagementapispringboot.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class S3Service {
    private final S3Client s3Client;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public FileResponse uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

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

        GetUrlRequest urlRequest = GetUrlRequest.builder().bucket(bucketName)
                .key(generatedFileName)
                .build();

        String fileUrl = s3Client.utilities().getUrl(urlRequest).toExternalForm();
        String fileSize = Long.toString(file.getSize());

        return new FileResponse(generatedFileName, fileSize, fileUrl);
    }

    public String deleteFile(String fileName) {
        try{
            s3Client.headObject(HeadObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(fileName).build()
            );
            s3Client.deleteObject(DeleteObjectRequest.builder()
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

//    public  byte[] downloadFile(String key) {
//        ResponseBytes<GetObjectResponse> objectAsByte = s3Client.getObjectAsBytes(GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .build()
//        );
//        return objectAsByte.asByteArray();
//    }
}
