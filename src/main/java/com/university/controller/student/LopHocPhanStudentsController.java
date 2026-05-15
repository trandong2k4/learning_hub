package com.university.controller.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.university.dto.response.student.LichStudentsResponseDTO;
import com.university.dto.response.student.LopHocPhanStudentsResponseDTO;
import com.university.service.student.LichStudentsService;
import com.university.service.student.LopHocPhanStudentsService;

import lombok.RequiredArgsConstructor;
import com.university.annotation.RequirePermission;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student/lop-hoc-phan")
@RequiredArgsConstructor
@RequirePermission("STU_CREDIT_REG_VIEW")
public class LopHocPhanStudentsController {

    private final LopHocPhanStudentsService lopHocPhanStudentsService;
    private final LichStudentsService lichStudentsService;

    @GetMapping
    public ResponseEntity<Page<LopHocPhanStudentsResponseDTO>> search(
            @RequestParam(required = false) UUID hocKiId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(lopHocPhanStudentsService.searchMoDangKy(
                hocKiId, keyword, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}/lich")
    public ResponseEntity<List<LichStudentsResponseDTO>> getLich(@PathVariable UUID id) {
        return ResponseEntity.ok(lichStudentsService.getLichByLopHocPhan(id));
    }
}
