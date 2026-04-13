package com.university.controller.student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.university.dto.response.student.TaiLieuStudentsResponseDTO;
import com.university.service.student.TaiLieuStudentsService;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/student/tailieu")
@RequiredArgsConstructor
public class TaiLieuStudentsController {

    private final TaiLieuStudentsService taiLieuStudentsService;
    @GetMapping("/{lopHocPhanId}")
    public ResponseEntity<List<TaiLieuStudentsResponseDTO>> getDanhSachTaiLieu(@PathVariable UUID lopHocPhanId) {
        return ResponseEntity.ok(taiLieuStudentsService.getDanhSachTaiLieu(lopHocPhanId));
    }
    @GetMapping("/search")
    public ResponseEntity<List<TaiLieuStudentsResponseDTO>> searchTaiLieu(
            @RequestParam UUID lopHocPhanId,
            @RequestParam String keyword,
            @RequestParam String loaiTaiLieu) {
        return ResponseEntity.ok(taiLieuStudentsService.searchTaiLieu(lopHocPhanId, keyword, loaiTaiLieu));
    }
}
