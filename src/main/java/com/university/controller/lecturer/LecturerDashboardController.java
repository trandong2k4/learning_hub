package com.university.controller.lecturer;

import com.university.annotation.RequirePermission;
import com.university.dto.response.lecturer.LecturerDashboardResponseDTO;
import com.university.service.lecturer.LecturerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/lecturer")
@CrossOrigin
@RequiredArgsConstructor
@RequirePermission("LECTURER_TEACHING")
public class LecturerDashboardController {

    private final LecturerDashboardService dashboardService;

    @GetMapping("/dashboard/{userId}")
    @PreAuthorize("@permissionService.hasPermission(#userId, 'LECTURER_TEACHING')")
    public ResponseEntity<LecturerDashboardResponseDTO> getDashboard(@PathVariable UUID userId) {
        return ResponseEntity.ok(dashboardService.getDashboard(userId));
    }
}
