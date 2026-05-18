package com.university.controller.lecturer;

import com.university.annotation.RequirePermission;
import com.university.dto.request.lecturer.AssignmentRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.lecturer.AssignmentResponseDTO;
import com.university.dto.response.lecturer.SubmissionDetailResponseDTO;
import com.university.dto.response.lecturer.SubmissionResponseDTO;
import com.university.service.lecturer.LecturerAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lecturer")
@CrossOrigin
@RequiredArgsConstructor
@RequirePermission("LECTURER_ASSESSMENT")
public class LecturerAssignmentController {

    private final LecturerAssignmentService assignmentService;

    @GetMapping("/assignments/{lopHocPhanId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<List<AssignmentResponseDTO>> getAssignments(
            @PathVariable UUID lopHocPhanId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(assignmentService.getAssignments(lopHocPhanId, userId));
    }

    @PostMapping("/assignments")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<AssignmentResponseDTO> createAssignment(
            @RequestParam UUID userId,
            @Valid @RequestBody AssignmentRequestDTO request) {
        return ResponseEntity.ok(assignmentService.createAssignment(userId, request));
    }

    @PostMapping("/assignments/import-excel")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<ExcelImportResult> importAssignments(
            @RequestParam UUID userId,
            @RequestParam UUID lopHocPhanId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(assignmentService.importAssignmentsFromExcel(userId, lopHocPhanId, file));
    }

    @PutMapping("/assignments/{assignmentId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<AssignmentResponseDTO> updateAssignment(
            @PathVariable UUID assignmentId,
            @RequestParam UUID userId,
            @Valid @RequestBody AssignmentRequestDTO request) {
        return ResponseEntity.ok(assignmentService.updateAssignment(userId, assignmentId, request));
    }

    @DeleteMapping("/assignments/{assignmentId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<String> deleteAssignment(
            @PathVariable UUID assignmentId,
            @RequestParam UUID userId) {
        assignmentService.deleteAssignment(userId, assignmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/submissions/{lopHocPhanId}/{assignmentId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<List<SubmissionResponseDTO>> getSubmissions(
            @PathVariable UUID lopHocPhanId,
            @PathVariable UUID assignmentId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(assignmentService.getSubmissions(lopHocPhanId, assignmentId, userId));
    }

    @GetMapping("/submissions/{submissionId}/detail")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<SubmissionResponseDTO> getSubmissionDetail(
            @PathVariable UUID submissionId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(assignmentService.getSubmissionDetail(submissionId, userId));
    }

    @GetMapping("/submissions/{submissionId}/detail-full")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<SubmissionDetailResponseDTO> getSubmissionDetailFull(
            @PathVariable UUID submissionId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(assignmentService.getSubmissionDetailFull(submissionId, userId));
    }

    @PutMapping("/submissions/{submissionId}/grade")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<Void> gradeSubmission(
            @PathVariable UUID submissionId,
            @RequestParam UUID userId,
            @RequestParam Double diem,
            @RequestParam(required = false) String feedback) {
        assignmentService.gradeSubmission(submissionId, userId, diem, feedback);
        return ResponseEntity.ok().build();
    }
}
