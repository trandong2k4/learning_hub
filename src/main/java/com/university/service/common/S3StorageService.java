package com.university.service.common;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private static final DateTimeFormatter PATH_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM");

    private final S3Client s3Client;

    @Value("${supabase.storage.bucket}")
    private String bucket;

    @Value("${supabase.storage.path-prefix:documents}")
    private String pathPrefix;

    public StoredObject upload(MultipartFile file, String extension) throws IOException {
        String normalizedExtension = extension.toLowerCase(Locale.ROOT);
        String storedName = UUID.randomUUID() + "." + normalizedExtension;
        String key = buildObjectKey(storedName);
        @SuppressWarnings("null")
        String contentType = file.getContentType() == null || file.getContentType().isBlank()
                ? "application/octet-stream"
                : file.getContentType();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .contentLength(file.getSize())
                .contentDisposition("inline; filename=\"" + storedName + "\"")
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return new StoredObject(key, storedName, file.getSize(), contentType);
    }

    public ResponseInputStream<GetObjectResponse> download(String key) {
        return s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(normalizeKey(key))
                .build());
    }

    public void delete(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(normalizeKey(key))
                .build());
    }

    private String buildObjectKey(String storedName) {
        String prefix = pathPrefix == null ? "" : pathPrefix.trim().replace("\\", "/");
        while (prefix.startsWith("/")) {
            prefix = prefix.substring(1);
        }
        while (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }

        String datedName = PATH_DATE_FORMAT.format(LocalDate.now()) + "/" + storedName;
        return prefix.isBlank() ? datedName : prefix + "/" + datedName;
    }

    private String normalizeKey(String key) {
        String normalized = key.trim();
        String s3Prefix = "s3://" + bucket + "/";
        if (normalized.startsWith(s3Prefix)) {
            normalized = normalized.substring(s3Prefix.length());
        }
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    public record StoredObject(String key, String storedName, long size, String contentType) {
    }
}
