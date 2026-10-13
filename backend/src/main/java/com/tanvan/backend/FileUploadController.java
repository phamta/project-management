package com.tanvan.backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;

    public FileUploadController(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        // Tạo tên file duy nhất để tránh ghi đè
        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest putRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(file.getContentType())
            .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

        return "Uploaded: " + key;
    }

    /**
     * API 1: Liệt kê tất cả file trong bucket (để frontend render danh sách gallery)
     * GET /api/files
     */
    @GetMapping
    public List<String> listFiles() {
        return s3Client.listObjectsV2(b -> b.bucket(bucketName))
            .contents()
            .stream()
            .map(S3Object::key)
            .collect(Collectors.toList());
    }

    /**
     * API 2: Proxy stream ảnh từ Floci về frontend
     * GET /api/files/{key}
     * Frontend chỉ cần trỏ <img src="http://localhost:8080/api/files/abc.jpg">
     */
    @GetMapping("/{key}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String key) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        ResponseInputStream<GetObjectResponse> s3Stream = s3Client.getObject(getRequest);
        GetObjectResponse metadata = s3Stream.response();

        // Lấy content-type gốc của file (image/jpeg, image/png, ...)
        String contentType = metadata.contentType() != null
            ? metadata.contentType()
            : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .contentLength(metadata.contentLength())
            // Cho phép browser cache ảnh trong 1 giờ
            .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
            .body(new InputStreamResource(s3Stream));
    }
}