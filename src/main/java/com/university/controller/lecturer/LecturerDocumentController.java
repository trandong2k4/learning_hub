package com.university.controller.lecturer;

import com.university.annotation.RequirePermission;
import com.university.dto.request.lecturer.DocumentRequestDTO;
import com.university.dto.response.lecturer.DocumentResponseDTO;
import com.university.service.lecturer.LecturerDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lecturer")
@CrossOrigin
@RequiredArgsConstructor
@RequirePermission("LECTURER_DOCUMENT")
public class LecturerDocumentController {

    private final LecturerDocumentService documentService;

    @GetMapping("/documents/{lopHocPhanId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_DOCUMENT')")
    public ResponseEntity<List<DocumentResponseDTO>> getDocuments(
            @PathVariable UUID lopHocPhanId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(documentService.getDocuments(lopHocPhanId, userId));
    }

    @PostMapping("/documents")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_DOCUMENT')")
    public ResponseEntity<DocumentResponseDTO> createDocument(
            @RequestParam UUID userId,
            @Valid @RequestBody DocumentRequestDTO request) {
        return ResponseEntity.ok(documentService.createDocument(userId, request));
    }

    @PutMapping("/documents/{documentId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_DOCUMENT')")
    public ResponseEntity<DocumentResponseDTO> updateDocument(
            @PathVariable UUID documentId,
            @RequestParam UUID userId,
            @Valid @RequestBody DocumentRequestDTO request) {
        return ResponseEntity.ok(documentService.updateDocument(userId, documentId, request));
    }

    @DeleteMapping("/documents/{documentId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_DOCUMENT')")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable UUID documentId,
            @RequestParam UUID userId) {
        documentService.deleteDocument(userId, documentId);
        return ResponseEntity.noContent().build();
    }
}
