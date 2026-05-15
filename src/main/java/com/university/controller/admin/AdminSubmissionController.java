package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.response.admin.AdminSubmissionResponseDTO;
import com.university.service.admin.AdminSubmissionService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/submissions")
@RequiredArgsConstructor
@RequirePermission("ADMIN_DASHBOARD_ADMIN_VIEW")
public class AdminSubmissionController {

    private final AdminSubmissionService submissionService;

    @GetMapping
    public ResponseEntity<Page<AdminSubmissionResponseDTO>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID lopHocPhanId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(submissionService.search(keyword, lopHocPhanId, page, size));
    }
}
