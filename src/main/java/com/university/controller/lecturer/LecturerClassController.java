package com.university.controller.lecturer;

import com.university.annotation.RequirePermission;
import com.university.dto.response.lecturer.LecturerClassDetailResponseDTO;
import com.university.dto.response.lecturer.LecturerClassSummaryResponseDTO;
import com.university.service.lecturer.LecturerClassService;
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
@RequirePermission("LECTURER_TEACHING")
public class LecturerClassController {

    private final LecturerClassService classService;

    @GetMapping("/classes/{userId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_TEACHING')")
    public ResponseEntity<List<LecturerClassSummaryResponseDTO>> getClasses(@PathVariable UUID userId) {
        return ResponseEntity.ok(classService.getClasses(userId));
    }

    @GetMapping("/classes/{lopHocPhanId}/detail")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_TEACHING')")
    public ResponseEntity<LecturerClassDetailResponseDTO> getClassDetail(
            @PathVariable UUID lopHocPhanId,
            @RequestParam UUID userId,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(classService.getClassDetail(userId, lopHocPhanId, keyword));
    }
}
