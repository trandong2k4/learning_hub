package com.university.controller.student;

import com.university.dto.request.student.DanhGiaGiangVienStudentRequest;
import com.university.dto.response.student.DanhGiaGiangVienStudentResponse;
import com.university.service.student.DanhGiaGiangVienStudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student/danh-gia-giang-vien")
@RequiredArgsConstructor
public class DanhGiaGiangVienStudentController {

    private final DanhGiaGiangVienStudentService danhGiaGiangVienStudentService;

    @GetMapping
    public ResponseEntity<List<DanhGiaGiangVienStudentResponse>> getDanhSachDanhGia() {
        return ResponseEntity.ok(danhGiaGiangVienStudentService.getDanhSachDanhGia());
    }

    @PostMapping("/draft")
    public ResponseEntity<DanhGiaGiangVienStudentResponse> saveDraft(
            @Valid @RequestBody DanhGiaGiangVienStudentRequest request) {
        return ResponseEntity.ok(danhGiaGiangVienStudentService.saveDraft(request));
    }

    @PostMapping("/submit")
    public ResponseEntity<DanhGiaGiangVienStudentResponse> submit(
            @Valid @RequestBody DanhGiaGiangVienStudentRequest request) {
        return ResponseEntity.ok(danhGiaGiangVienStudentService.submit(request));
    }
}
