package com.university.controller.common;

import com.university.config.SecurityUtils;
import com.university.dto.response.common.FileUploadResponseDTO;
import com.university.entity.FileStorage;
import com.university.entity.Users;
import com.university.enums.FileEnum;
import com.university.repository.admin.UsersAdminRepository;
import com.university.repository.common.FileStorageRepository;
import com.university.service.common.S3StorageService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "zip", "rar", "png", "jpg", "jpeg", "webp", "txt");

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private final S3StorageService s3StorageService;
    private final FileStorageRepository fileStorageRepository;
    private final UsersAdminRepository usersRepository;

    public FileUploadController(
            S3StorageService s3StorageService,
            FileStorageRepository fileStorageRepository,
            UsersAdminRepository usersRepository) {
        this.s3StorageService = s3StorageService;
        this.fileStorageRepository = fileStorageRepository;
        this.usersRepository = usersRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponseDTO> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống.");
        }

        @SuppressWarnings("null")
        String originalName = StringUtils
                .cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String extension = getExtension(originalName);
        if (extension.isBlank() || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Định dạng file không được hỗ trợ.");
        }

        S3StorageService.StoredObject storedObject = s3StorageService.upload(file, extension);

        FileStorage metadata = new FileStorage();
        metadata.setFileName(storedObject.storedName());
        metadata.setFileType(resolveFileType(extension, storedObject.contentType()));
        metadata.setFileSize((float) storedObject.size());
        metadata.setFileUrl(storedObject.key());
        metadata.setUsers(resolveCurrentUser());
        metadata.setCreatedAt(LocalDateTime.now());

        FileStorage saved = fileStorageRepository.save(metadata);
        String downloadUrl = "/api/files/" + saved.getId() + "/download";

        return ResponseEntity.ok(new FileUploadResponseDTO(
                saved.getId(),
                storedObject.storedName(),
                originalName,
                downloadUrl,
                storedObject.key(),
                file.getSize(),
                file.getContentType()));
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable UUID fileId) {
        FileStorage fileStorage = fileStorageRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File không tồn tại."));

        ResponseInputStream<GetObjectResponse> stream = s3StorageService.download(fileStorage.getFileUrl());
        GetObjectResponse object = stream.response();
        String contentType = object.contentType() == null || object.contentType().isBlank()
                ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                : object.contentType();

        ResponseEntity.BodyBuilder response = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileStorage.getFileName() + "\"");

        if (object.contentLength() != null) {
            response.contentLength(object.contentLength());
        }

        return response.body(new InputStreamResource(stream));
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> view(@PathVariable String fileName) throws MalformedURLException {
        var storageFile = fileStorageRepository.findByFileName(fileName);
        if (storageFile.isPresent()) {
            FileStorage fileStorage = storageFile.get();
            ResponseInputStream<GetObjectResponse> stream = s3StorageService.download(fileStorage.getFileUrl());
            GetObjectResponse object = stream.response();
            String contentType = object.contentType() == null || object.contentType().isBlank()
                    ? MediaType.APPLICATION_OCTET_STREAM_VALUE
                    : object.contentType();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileStorage.getFileName() + "\"")
                    .body((Resource) new InputStreamResource(stream));
        }

        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path filePath = root.resolve(fileName).normalize();
        if (!filePath.startsWith(root) || !Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        String contentType;
        try {
            contentType = Files.probeContentType(filePath);
        } catch (IOException ex) {
            contentType = null;
        }
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private String getExtension(String fileName) {
        int idx = fileName.lastIndexOf('.');
        return idx >= 0 && idx < fileName.length() - 1 ? fileName.substring(idx + 1) : "";
    }

    private Users resolveCurrentUser() {
        try {
            return usersRepository.findById(SecurityUtils.getCurrentUserId()).orElse(null);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private FileEnum resolveFileType(String extension, String contentType) {
        String ext = extension == null ? "" : extension.toLowerCase(Locale.ROOT);
        String type = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
        if (type.startsWith("video/") || Set.of("mp4", "avi", "mov", "mkv", "webm").contains(ext)) {
            return FileEnum.VIDEO;
        }
        if (type.startsWith("audio/") || Set.of("mp3", "wav", "ogg").contains(ext)) {
            return FileEnum.AUDIO;
        }
        if (Set.of("pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "zip", "rar", "txt").contains(ext)) {
            return FileEnum.DOCUMENT;
        }
        if (type.startsWith("image/")) {
            return FileEnum.OTHER;
        }
        return FileEnum.OTHER;
    }
}
