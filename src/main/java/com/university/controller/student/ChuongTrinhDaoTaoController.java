package com.university.controller.student;
import com.university.dto.request.student.ChuongTrinhDaoTaoRequestDTO;
import com.university.dto.response.student.ChuongTrinhDaoTaoResponseDTO;
import com.university.service.student.ChuongTrinhDaoTaoService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/student/chuong-trinh-dao-tao")
@RequiredArgsConstructor
public class ChuongTrinhDaoTaoController {
private final ChuongTrinhDaoTaoService chuongTrinhDaoTaoService;

    @PostMapping("/search")
    public ResponseEntity<List<ChuongTrinhDaoTaoResponseDTO>> getDanhSach(@RequestBody ChuongTrinhDaoTaoRequestDTO request) {
        return ResponseEntity.ok(chuongTrinhDaoTaoService.getDanhSach(request));
    }
}
