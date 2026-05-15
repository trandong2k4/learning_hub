package com.university.controller.student;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.university.dto.response.student.ExerciseDetailResponseDTO;
import com.university.dto.response.student.ExerciseStudentsResponseDTO;
import com.university.service.student.ExerciseStudentsService;

import lombok.RequiredArgsConstructor;
import com.university.annotation.RequirePermission;

@RestController
@RequestMapping("/api/student/exercises")
@RequiredArgsConstructor
@RequirePermission("STU_EXERCISE_LIST_VIEW")
public class ExerciseStudentsController {

        private final ExerciseStudentsService exerciseStudentsService;

        @GetMapping
        public ResponseEntity<Page<ExerciseStudentsResponseDTO>> getDanhSachBaiTap(
                        @RequestParam UUID lopHocPhanId,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                validateUUID(lopHocPhanId, "lopHocPhanId");

                return ResponseEntity.ok(
                                exerciseStudentsService.getDanhSachBaiTap(lopHocPhanId, keyword, page, size));
        }

        @GetMapping("/{id}")
        public ResponseEntity<ExerciseDetailResponseDTO> getChiTietBaiTap(@PathVariable UUID id) {
                validateUUID(id, "exerciseId");

                return ResponseEntity.ok(exerciseStudentsService.getChiTietBaiTap(id));
        }

        @GetMapping("/status/sap-mo")
        public ResponseEntity<List<ExerciseStudentsResponseDTO>> getBaiTapSapMo(
                        @RequestParam UUID lopHocPhanId) {

                validateUUID(lopHocPhanId, "lopHocPhanId");

                return ResponseEntity.ok(exerciseStudentsService.getBaiTapSapMo(lopHocPhanId));
        }

        @GetMapping("/status/dang-mo")
        public ResponseEntity<List<ExerciseStudentsResponseDTO>> getBaiTapDangMo(
                        @RequestParam UUID lopHocPhanId) {

                validateUUID(lopHocPhanId, "lopHocPhanId");

                return ResponseEntity.ok(exerciseStudentsService.getBaiTapDangMo(lopHocPhanId));
        }

        @GetMapping("/status/da-dong")
        public ResponseEntity<List<ExerciseStudentsResponseDTO>> getBaiTapDaDong(
                        @RequestParam UUID lopHocPhanId) {

                validateUUID(lopHocPhanId, "lopHocPhanId");

                return ResponseEntity.ok(exerciseStudentsService.getBaiTapDaDong(lopHocPhanId));
        }

        @GetMapping("/{id}/is-open")
        public ResponseEntity<Boolean> isExerciseOpen(@PathVariable UUID id) {
                validateUUID(id, "exerciseId");

                return ResponseEntity.ok(exerciseStudentsService.isExerciseOpen(id));
        }

        @GetMapping("/{id}/has-result")
        public ResponseEntity<Boolean> hasExerciseResult(@PathVariable UUID id) {
                validateUUID(id, "exerciseId");

                return ResponseEntity.ok(exerciseStudentsService.hasExerciseResult(id));
        }

        @GetMapping("/{id}/can-edit")
        public ResponseEntity<Boolean> canEditExercise(@PathVariable UUID id) {
                validateUUID(id, "exerciseId");

                return ResponseEntity.ok(exerciseStudentsService.canEditExercise(id));
        }

        private void validateUUID(UUID id, String fieldName) {
                if (id == null) {
                        throw new IllegalArgumentException(fieldName + " khong duoc null");
                }
        }
}
