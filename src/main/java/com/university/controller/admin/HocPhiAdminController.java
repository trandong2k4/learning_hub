package com.university.controller.admin;

import com.university.annotation.RequirePermission;
import com.university.dto.response.admin.HocPhiAdminResponseDTO;
import com.university.service.admin.HocPhiAdminService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/hoc-phi")
@RequiredArgsConstructor
@RequirePermission("ADMIN_TUITION_VIEW")
public class HocPhiAdminController {

    private final HocPhiAdminService hocPhiService;

    @GetMapping
    public ResponseEntity<List<HocPhiAdminResponseDTO>> getAll() {
        return ResponseEntity.ok(hocPhiService.getAllHocPhi());
    }

    @GetMapping("/view")
    public ResponseEntity<List<HocPhiAdminResponseDTO.HocPhiView>> getAllView() {
        return ResponseEntity.ok(hocPhiService.getAllHocPhiView());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HocPhiAdminResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(hocPhiService.getById(id));
    }

    @GetMapping("/hoc-ki/{hocKiId}")
    public ResponseEntity<List<HocPhiAdminResponseDTO>> getByHocKi(@PathVariable UUID hocKiId) {
        return ResponseEntity.ok(hocPhiService.getHocPhiByHocKi(hocKiId));
    }

    @GetMapping("/hoc-vien/{hocVienId}")
    public ResponseEntity<List<HocPhiAdminResponseDTO>> getByHocVien(@PathVariable UUID hocVienId) {
        return ResponseEntity.ok(hocPhiService.getHocPhiByHocVien(hocVienId));
    }

    @GetMapping("/dashboard/tong-quan")
    public ResponseEntity<HocPhiAdminResponseDTO.DashboardTongQuan> getDashboardTongQuan() {
        return ResponseEntity.ok(hocPhiService.getDashboardTongQuan());
    }

    @GetMapping("/dashboard/theo-hoc-ki")
    public ResponseEntity<List<HocPhiAdminResponseDTO.DashboardTheoHocKi>> getDashboardTheoHocKi() {
        return ResponseEntity.ok(hocPhiService.getDashboardTheoHocKi());
    }

    @GetMapping("/dashboard/theo-thang")
    public ResponseEntity<List<HocPhiAdminResponseDTO.DashboardTheoThang>> getDashboardTheoThang() {
        return ResponseEntity.ok(hocPhiService.getDashboardTheoThang());
    }

    @GetMapping("/dashboard/top-no")
    public ResponseEntity<List<HocPhiAdminResponseDTO.DashboardTopNo>> getDashboardTopNo() {
        return ResponseEntity.ok(hocPhiService.getDashboardTopNo());
    }

    @GetMapping("/hoc-vien/{hocVienId}/tong-tin-chi")
    public ResponseEntity<Long> getTongTinChiByHocVien(@PathVariable UUID hocVienId) {
        return ResponseEntity.ok(hocPhiService.getTongTinChiByHocVien(hocVienId));
    }

    @GetMapping("/hoc-vien/{hocVienId}/hoc-ki/{hocKiId}/tong-tin-chi")
    public ResponseEntity<Long> getTongTinChiByHocVienAndHocKi(
            @PathVariable UUID hocVienId,
            @PathVariable UUID hocKiId) {
        return ResponseEntity.ok(hocPhiService.getTongTinChiByHocVienAndHocKi(hocVienId, hocKiId));
    }

    @GetMapping("/dang-ky-tin-chi")
    public ResponseEntity<List<HocPhiAdminResponseDTO.DangKyTinChiItem>> getDangKyTinChiAll() {
        return ResponseEntity.ok(hocPhiService.getDangKyTinChiAll());
    }

    @GetMapping("/dang-ky-tin-chi/hoc-ki/{hocKiId}")
    public ResponseEntity<List<HocPhiAdminResponseDTO.DangKyTinChiItem>> getDangKyTinChiByHocKi(
            @PathVariable UUID hocKiId) {
        return ResponseEntity.ok(hocPhiService.getDangKyTinChiByHocKi(hocKiId));
    }

    @GetMapping("/dang-ky-tin-chi/hoc-vien/{hocVienId}")
    public ResponseEntity<List<HocPhiAdminResponseDTO.DangKyTinChiItem>> getDangKyTinChiByHocVien(
            @PathVariable UUID hocVienId) {
        return ResponseEntity.ok(hocPhiService.getDangKyTinChiByHocVien(hocVienId));
    }

    @GetMapping("/dang-ky-tin-chi/dashboard/tong-quan")
    public ResponseEntity<HocPhiAdminResponseDTO.DangKyTinChiTongQuan> getDangKyTinChiTongQuan() {
        return ResponseEntity.ok(hocPhiService.getDangKyTinChiTongQuan());
    }

    @GetMapping("/dang-ky-tin-chi/dashboard/theo-hoc-ki")
    public ResponseEntity<List<HocPhiAdminResponseDTO.DangKyTinChiTheoHocKi>> getDangKyTinChiTheoHocKi() {
        return ResponseEntity.ok(hocPhiService.getDangKyTinChiTongHopTheoHocKi());
    }

    @GetMapping("/dang-ky-tin-chi/dashboard/theo-nam-hoc")
    public ResponseEntity<List<HocPhiAdminResponseDTO.DangKyTinChiTheoNamHoc>> getDangKyTinChiTheoNamHoc() {
        return ResponseEntity.ok(hocPhiService.getDangKyTinChiTongHopTheoNamHoc());
    }
}
