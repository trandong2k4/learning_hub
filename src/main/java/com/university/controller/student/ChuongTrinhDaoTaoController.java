package com.university.controller.student;

import com.university.dto.request.student.ChuongTrinhDaoTaoRequestDTO;
import com.university.dto.response.student.ChuongTrinhDaoTaoResponseDTO;
import com.university.service.student.ChuongTrinhDaoTaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import com.university.annotation.RequirePermission;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/chuong-trinh-dao-tao")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_student')")
@RequirePermission("STU_CURRICULUM_SEARCH")
public class ChuongTrinhDaoTaoController {

    private final ChuongTrinhDaoTaoService chuongTrinhDaoTaoService;

    @PostMapping("/search")
    public ResponseEntity<Page<ChuongTrinhDaoTaoResponseDTO>> getDanhSach(
            @RequestBody @Valid ChuongTrinhDaoTaoRequestDTO request,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(chuongTrinhDaoTaoService.getDanhSach(request, pageable));
    }
}
