package com.university.controller.student;

import com.university.dto.response.student.DashboardStudentResponse;
import com.university.service.student.DashboardStudentService;
import lombok.RequiredArgsConstructor;
import com.university.annotation.RequirePermission;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_student')")
@RequirePermission("STU_DASH_LEARN_PROGRESS")
public class DashboardStudentController {

    private final DashboardStudentService dashboardStudentService;

    @GetMapping
    public ResponseEntity<DashboardStudentResponse> getDashboard() {
        return ResponseEntity.ok(dashboardStudentService.getDashboard());
    }
}
