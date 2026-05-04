package com.university.controller.student;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.university.config.SecurityUtils;
import com.university.dto.response.student.ExerciseStudentsResponseDTO;
import com.university.service.student.ExerciseStudentsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student/exercises")
@RequiredArgsConstructor
public class ExerciseStudentsController {

    private final ExerciseStudentsService exerciseStudentsService;

    // ================= DANH SÁCH =================
    @GetMapping
    public ResponseEntity<Page<ExerciseStudentsResponseDTO>> getDanhSachBaiTap(
            @RequestParam UUID lopHocPhanId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        validateUUID(lopHocPhanId, "lopHocPhanId");

        UUID hocVienId = SecurityUtils.getCurrentHocVienId(); // 🔥

        return ResponseEntity.ok(
                exerciseStudentsService.getDanhSachBaiTap(
                        lopHocPhanId, hocVienId, keyword, page, size
                )
        );
    }

    // ================= CHI TIẾT =================
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseStudentsResponseDTO> getChiTietBaiTap(
            @PathVariable UUID id) {

        validateUUID(id, "exerciseId");

        UUID hocVienId = SecurityUtils.getCurrentHocVienId();

        return ResponseEntity.ok(
                exerciseStudentsService.getChiTietBaiTap(id, hocVienId)
        );
    }

    // ================= SẮP MỞ =================
    @GetMapping("/status/sap-mo")
    public ResponseEntity<List<ExerciseStudentsResponseDTO>> getBaiTapSapMo(
            @RequestParam UUID lopHocPhanId) {

        validateUUID(lopHocPhanId, "lopHocPhanId");

        UUID hocVienId = SecurityUtils.getCurrentHocVienId();

        return ResponseEntity.ok(
                exerciseStudentsService.getBaiTapSapMo(lopHocPhanId, hocVienId)
        );
    }

    // ================= ĐANG MỞ =================
    @GetMapping("/status/dang-mo")
    public ResponseEntity<List<ExerciseStudentsResponseDTO>> getBaiTapDangMo(
            @RequestParam UUID lopHocPhanId) {

        validateUUID(lopHocPhanId, "lopHocPhanId");

        UUID hocVienId = SecurityUtils.getCurrentHocVienId();

        return ResponseEntity.ok(
                exerciseStudentsService.getBaiTapDangMo(lopHocPhanId, hocVienId)
        );
    }

    // ================= ĐÃ ĐÓNG =================
    @GetMapping("/status/da-dong")
    public ResponseEntity<List<ExerciseStudentsResponseDTO>> getBaiTapDaDong(
            @RequestParam UUID lopHocPhanId) {

        validateUUID(lopHocPhanId, "lopHocPhanId");

        UUID hocVienId = SecurityUtils.getCurrentHocVienId();

        return ResponseEntity.ok(
                exerciseStudentsService.getBaiTapDaDong(lopHocPhanId, hocVienId)
        );
    }

    // ================= CHECK ĐANG MỞ =================
    @GetMapping("/{id}/is-open")
    public ResponseEntity<Boolean> isExerciseOpen(
            @PathVariable UUID id) {

        validateUUID(id, "exerciseId");

        return ResponseEntity.ok(
                exerciseStudentsService.isExerciseOpen(id)
        );
    }

    // ================= CHECK CÓ KẾT QUẢ =================
    @GetMapping("/{id}/has-result")
    public ResponseEntity<Boolean> hasExerciseResult(
            @PathVariable UUID id) {

        validateUUID(id, "exerciseId");

        UUID hocVienId = SecurityUtils.getCurrentHocVienId();

        return ResponseEntity.ok(
                exerciseStudentsService.hasExerciseResult(id, hocVienId)
        );
    }

    // ================= CHECK CÓ ĐƯỢC EDIT KHÔNG =================
    @GetMapping("/{id}/can-edit")
    public ResponseEntity<Boolean> canEditExercise(
            @PathVariable UUID id) {

        validateUUID(id, "exerciseId");

        UUID hocVienId = SecurityUtils.getCurrentHocVienId();

        return ResponseEntity.ok(
                exerciseStudentsService.canEditExercise(id, hocVienId)
        );
    }

    // ================= HELPER =================
    private void validateUUID(UUID id, String fieldName) {
        if (id == null) {
            throw new IllegalArgumentException(fieldName + " không được null");
        }
    }
}