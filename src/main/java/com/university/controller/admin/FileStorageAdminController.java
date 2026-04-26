package com.university.controller.admin;

import com.university.dto.request.admin.FileStorageAdminRequestDTO;
import com.university.dto.response.admin.FileStorageAdminResponseDTO;
import com.university.service.admin.FileStorageAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/file-storage")
@RequiredArgsConstructor
public class FileStorageAdminController {

    private final FileStorageAdminService fileStorageService;

    @PostMapping
    public ResponseEntity<FileStorageAdminResponseDTO> createFileStorage(
            @Valid @RequestBody FileStorageAdminRequestDTO request) {
        FileStorageAdminResponseDTO response = fileStorageService.createFileStorage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FileStorageAdminResponseDTO> updateFileStorage(
            @PathVariable UUID id,
            @Valid @RequestBody FileStorageAdminRequestDTO request) {
        FileStorageAdminResponseDTO response = fileStorageService.updateFileStorage(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileStorageAdminResponseDTO> getFileStorageById(@PathVariable UUID id) {
        FileStorageAdminResponseDTO response = fileStorageService.getFileStorageById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FileStorageAdminResponseDTO.FileStorageView>> getAllFileStorage() {
        List<FileStorageAdminResponseDTO.FileStorageView> response = fileStorageService.getAllFileStorage();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFileStorage(@PathVariable UUID id) {
        fileStorageService.deleteFileStorage(id);
        return ResponseEntity.noContent().build();
    }
}
