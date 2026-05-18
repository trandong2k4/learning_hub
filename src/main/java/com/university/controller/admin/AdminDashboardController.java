package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.response.admin.AdminDashboardResponseDTO;
import com.university.service.admin.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@RequirePermission("ADMIN_DASHBOARD_ADMIN_VIEW")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping
    public ResponseEntity<AdminDashboardResponseDTO> getDashboard() {
        return ResponseEntity.ok(adminDashboardService.getDashboard());
    }
}
