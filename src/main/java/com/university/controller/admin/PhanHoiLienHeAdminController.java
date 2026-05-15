package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.request.admin.PhanHoiLienHeAdminRequestDTO;
import com.university.dto.request.admin.PhanHoiLienHeReplyRequestDTO;
import com.university.dto.request.admin.PhanHoiLienHeStatusRequestDTO;
import com.university.dto.response.admin.PhanHoiLienHeAdminResponseDTO;
import com.university.enums.TrangThaiXuLyLienHeEnum;
import com.university.service.admin.PhanHoiLienHeAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/phan-hoi-lien-he")
@RequiredArgsConstructor
@RequirePermission("ADMIN_CONTACT_VIEW")
public class PhanHoiLienHeAdminController {

    private final PhanHoiLienHeAdminService phanHoiService;

    @PostMapping
    public ResponseEntity<PhanHoiLienHeAdminResponseDTO> create(
            @Valid @RequestBody PhanHoiLienHeAdminRequestDTO request) {
        PhanHoiLienHeAdminResponseDTO response = phanHoiService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PhanHoiLienHeAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(phanHoiService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PhanHoiLienHeAdminResponseDTO>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TrangThaiXuLyLienHeEnum trangThai) {
        return ResponseEntity.ok(phanHoiService.search(keyword, trangThai));
    }

    @GetMapping("/trang-thai/{trangThai}")
    public ResponseEntity<List<PhanHoiLienHeAdminResponseDTO>> getByTrangThai(
            @PathVariable TrangThaiXuLyLienHeEnum trangThai) {
        return ResponseEntity.ok(phanHoiService.getAllByTrangThai(trangThai));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PhanHoiLienHeAdminResponseDTO>> getByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(phanHoiService.getByDateRange(startDate, endDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhanHoiLienHeAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(phanHoiService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhanHoiLienHeAdminResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody PhanHoiLienHeAdminRequestDTO request) {
        return ResponseEntity.ok(phanHoiService.update(id, request));
    }

    @PutMapping("/{id}/trang-thai")
    public ResponseEntity<PhanHoiLienHeAdminResponseDTO> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody PhanHoiLienHeStatusRequestDTO request) {
        return ResponseEntity.ok(phanHoiService.updateStatus(id, request));
    }

    @PutMapping("/{id}/phan-hoi")
    public ResponseEntity<PhanHoiLienHeAdminResponseDTO> reply(
            @PathVariable UUID id,
            @Valid @RequestBody PhanHoiLienHeReplyRequestDTO request,
            Authentication authentication) {
        String nguoiTraLoi = authentication != null ? authentication.getName() : "Admin";
        return ResponseEntity.ok(phanHoiService.reply(id, request, nguoiTraLoi));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        phanHoiService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/by-list")
    public ResponseEntity<Map<String, String>> deleteList(@RequestBody List<UUID> ids) {
        phanHoiService.deleteAll(ids);
        return ResponseEntity.ok(Map.of("message", "Xóa thành công " + ids.size() + " phản hồi liên hệ"));
    }

    @GetMapping("/thong-ke")
    public ResponseEntity<Map<String, Long>> getThongKe() {
        long chuaXuLy = phanHoiService.countByTrangThai(TrangThaiXuLyLienHeEnum.CHUA_XU_LY);
        long dangXuLy = phanHoiService.countByTrangThai(TrangThaiXuLyLienHeEnum.DANG_XU_LY);
        long daXuLy = phanHoiService.countByTrangThai(TrangThaiXuLyLienHeEnum.DA_XU_LY);
        return ResponseEntity.ok(Map.of(
                "chuaXuLy", chuaXuLy,
                "dangXuLy", dangXuLy,
                "daXuLy", daXuLy,
                "tong", chuaXuLy + dangXuLy + daXuLy
        ));
    }
}
