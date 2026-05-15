package com.university.controller.lecturer;

import com.university.annotation.RequirePermission;
import com.university.dto.request.lecturer.AttendanceRequestDTO;
import com.university.dto.response.lecturer.AttendanceResponseDTO;
import com.university.service.lecturer.LecturerAttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/lecturer")
@CrossOrigin
@RequiredArgsConstructor
@RequirePermission("LECTURER_ASSESSMENT")
public class LecturerAttendanceController {

    private final LecturerAttendanceService attendanceService;

    @GetMapping("/attendance/{lopHocPhanId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<AttendanceResponseDTO> getAttendance(
            @PathVariable UUID lopHocPhanId,
            @RequestParam UUID userId,
            @RequestParam(required = false) String lichId) {
        return ResponseEntity.ok(attendanceService.getAttendance(lopHocPhanId, userId, lichId));
    }

    @PutMapping("/attendance")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_ASSESSMENT')")
    public ResponseEntity<Void> updateAttendance(
            @RequestParam UUID userId,
            @Valid @RequestBody AttendanceRequestDTO request) {
        attendanceService.updateAttendance(userId, request);
        return ResponseEntity.ok().build();
    }
}
