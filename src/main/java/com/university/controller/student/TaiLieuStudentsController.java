package com.university.controller.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import com.university.annotation.RequirePermission;
import org.springframework.web.bind.annotation.RequestParam;
import com.university.dto.response.student.TaiLieuStudentsResponseDTO;
import com.university.service.student.TaiLieuStudentsService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student/tailieu")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_student')")
@RequirePermission("STU_DOC_LIST_VIEW")
public class TaiLieuStudentsController {

    private final TaiLieuStudentsService taiLieuStudentsService;

    @GetMapping("/{lopHocPhanId}")
    public ResponseEntity<Page<TaiLieuStudentsResponseDTO>> getDanhSachTaiLieu(
            @PathVariable UUID lopHocPhanId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(taiLieuStudentsService.getDanhSachTaiLieu(lopHocPhanId, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TaiLieuStudentsResponseDTO>> searchTaiLieu(
            @RequestParam UUID lopHocPhanId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String loaiTaiLieu,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(taiLieuStudentsService.searchTaiLieu(lopHocPhanId, keyword, loaiTaiLieu, pageable));
    }
}
