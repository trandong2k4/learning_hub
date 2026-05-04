package com.university.controller.student;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.dto.request.student.DangKyTinChiRequestDTO;
import com.university.dto.response.student.DangKyTinChiResponseDTO;
import com.university.service.student.DangKyTinChiService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student/dang-ky-tin-chi")
@RequiredArgsConstructor
public class DangKyTinChiController {

    private final DangKyTinChiService dangKyTinChiService;

    @PostMapping
    public ResponseEntity<DangKyTinChiResponseDTO> dangKy(
            @RequestBody @Valid DangKyTinChiRequestDTO request) {
        return ResponseEntity.ok(dangKyTinChiService.dangKy(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> huyDangKyTinChi(@PathVariable UUID id) {
        dangKyTinChiService.huyDangKyTinChi(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<DangKyTinChiResponseDTO>> getMyDangKyTinChi() {
        return ResponseEntity.ok(dangKyTinChiService.getDangKyTinChiCuaToi());
    }
}
