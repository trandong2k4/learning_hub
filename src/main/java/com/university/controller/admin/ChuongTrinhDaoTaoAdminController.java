package com.university.controller.admin;

import com.university.dto.request.admin.BaiVietAdminRequestDTO;
import com.university.dto.request.admin.ChuongTrinhDaoTaoAdminRequestDTO;
import com.university.dto.response.admin.BaiVietAdminResponseDTO;
import com.university.dto.response.admin.ChuongTrinhDaoTaoAdminResponseDTO;
import com.university.service.admin.BaiVietAdminService;
import com.university.service.admin.ChuongTrinhDaoTaoAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/chuong-trinh-dao-tao")
@RequiredArgsConstructor
public class ChuongTrinhDaoTaoAdminController {

    private final ChuongTrinhDaoTaoAdminService chuongTrinhDaoTaoAdminService;
    private final BaiVietAdminService baiVietService;

    @PostMapping
    public ResponseEntity<ChuongTrinhDaoTaoAdminResponseDTO> create(
            @Valid @RequestBody ChuongTrinhDaoTaoAdminRequestDTO request) {
        ChuongTrinhDaoTaoAdminResponseDTO response = chuongTrinhDaoTaoAdminService.createCTDT(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaiVietAdminResponseDTO> updateBaiViet(
            @PathVariable UUID id,
            @Valid @RequestBody BaiVietAdminRequestDTO request) {
        BaiVietAdminResponseDTO response = baiVietService.updateBaiViet(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaiVietAdminResponseDTO> getBaiViet(@PathVariable UUID id) {
        BaiVietAdminResponseDTO response = baiVietService.getBaiVietById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BaiVietAdminResponseDTO.BaiVietView>> getAllBaiViet() {
        return ResponseEntity.ok(baiVietService.getALlBaiViet());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBaiViet(@PathVariable UUID id) {
        baiVietService.deleteBaiViet(id);
        return ResponseEntity.noContent().build();
    }
}
