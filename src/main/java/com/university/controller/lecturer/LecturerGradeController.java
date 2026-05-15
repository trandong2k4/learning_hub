package com.university.controller.lecturer;

import com.university.annotation.RequirePermission;
import com.university.dto.request.lecturer.CreateCotDiemRequestDTO;
import com.university.dto.request.lecturer.GradeRequestDTO;
import com.university.dto.response.lecturer.GradeHistoryResponseDTO;
import com.university.dto.response.lecturer.GradeResponseDTO;
import com.university.entity.CotDiem;
import com.university.service.lecturer.LecturerGradeHistoryService;
import com.university.service.lecturer.LecturerGradeService;
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
@RequirePermission("LECTURER_ASSESSMENT")
public class LecturerGradeController {

    private final LecturerGradeService gradeService;
    private final LecturerGradeHistoryService gradeHistoryService;

    @GetMapping("/grades/{lopHocPhanId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<GradeResponseDTO> getGrades(
            @PathVariable UUID lopHocPhanId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(gradeService.getGrades(lopHocPhanId, userId));
    }

    @PutMapping("/grades")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<Void> updateGrades(
            @RequestParam UUID userId,
            @Valid @RequestBody GradeRequestDTO request) {
        gradeService.updateGrades(userId, UUID.fromString(request.getLopHocPhanId()), request.getStudentGrades());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/grades/columns")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<CotDiem> createCotDiem(
            @RequestParam UUID userId,
            @Valid @RequestBody CreateCotDiemRequestDTO request) {
        return ResponseEntity.ok(gradeService.createCotDiem(userId, request));
    }

    @GetMapping("/grades/{lopHocPhanId}/history/{hocVienId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<List<GradeHistoryResponseDTO>> getGradeHistory(
            @PathVariable UUID lopHocPhanId,
            @PathVariable UUID hocVienId,
            @RequestParam UUID userId) {
        return ResponseEntity.ok(gradeHistoryService.getGradeHistory(lopHocPhanId, hocVienId, userId));
    }
}
