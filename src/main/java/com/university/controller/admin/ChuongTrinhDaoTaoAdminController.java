package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.ChuongTrinhDaoTaoAdminRequestDTO;
import com.university.dto.response.admin.ChuongTrinhDaoTaoAdminResponseDTO;
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
@RequirePermission("ADMIN_SUBJECT_VIEW")
public class ChuongTrinhDaoTaoAdminController {

    private final ChuongTrinhDaoTaoAdminService ctdtService;

    @GetMapping
    public ResponseEntity<List<ChuongTrinhDaoTaoAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(ctdtService.getAll());
    }

    @GetMapping("/by-nganh/{nganhId}")
    public ResponseEntity<List<ChuongTrinhDaoTaoAdminResponseDTO>> getByNganh(@PathVariable UUID nganhId) {
        return ResponseEntity.ok(ctdtService.getByNganhId(nganhId));
    }

    @GetMapping("/nganh-options")
    public ResponseEntity<List<ChuongTrinhDaoTaoAdminResponseDTO>> getNganhOptions() {
        return ResponseEntity.ok(ctdtService.getAllNganh());
    }

    @GetMapping("/monhoc-options")
    public ResponseEntity<List<ChuongTrinhDaoTaoAdminResponseDTO>> getMonHocOptions() {
        return ResponseEntity.ok(ctdtService.getAllMonHoc());
    }

    @PostMapping
    public ResponseEntity<ChuongTrinhDaoTaoAdminResponseDTO> create(
            @RequestBody @Valid ChuongTrinhDaoTaoAdminRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ctdtService.create(dto));
    }

    @PostMapping("/list")
    public ResponseEntity<List<ChuongTrinhDaoTaoAdminResponseDTO>> createList(
            @RequestBody @Valid List<ChuongTrinhDaoTaoAdminRequestDTO> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ctdtService.createList(dtos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        ctdtService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/by-list")
    public ResponseEntity<String> deleteList(@RequestBody List<UUID> ids) {
        ctdtService.deleteList(ids);
        return ResponseEntity.ok("Xóa thành công " + ids.size() + " chương trình đào tạo");
    }
}
