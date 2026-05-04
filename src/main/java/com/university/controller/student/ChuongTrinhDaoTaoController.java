package com.university.controller.student;

import com.university.dto.request.student.ChuongTrinhDaoTaoRequestDTO;
import com.university.dto.response.student.ChuongTrinhDaoTaoResponseDTO;
import com.university.service.student.ChuongTrinhDaoTaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student/chuong-trinh-dao-tao")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class ChuongTrinhDaoTaoController {

    private final ChuongTrinhDaoTaoService chuongTrinhDaoTaoService;

    @PostMapping("/search")
    public ResponseEntity<List<ChuongTrinhDaoTaoResponseDTO>> getDanhSach(
            @RequestBody @Valid ChuongTrinhDaoTaoRequestDTO request) {
        return ResponseEntity.ok(chuongTrinhDaoTaoService.getDanhSach(request));
    }
}
