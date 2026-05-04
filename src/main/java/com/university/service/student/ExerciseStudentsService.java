package com.university.service.student;

import java.util.UUID;
import org.springframework.data.domain.Page;
import com.university.dto.response.student.ExerciseStudentsResponseDTO;
import java.util.List;

public interface ExerciseStudentsService {

    // ================= DANH SÁCH =================
    Page<ExerciseStudentsResponseDTO> getDanhSachBaiTap(
            UUID lopHocPhanId,
            UUID hocVienId,
            String keyword,
            int page,
            int size
    );

    // ================= CHI TIẾT =================
    ExerciseStudentsResponseDTO getChiTietBaiTap(
            UUID exerciseId,
            UUID hocVienId
    );

    // ================= CHECK =================
    boolean isExerciseOpen(UUID exerciseId);

    // ================= DANH SÁCH THEO TRẠNG THÁI =================
    List<ExerciseStudentsResponseDTO> getBaiTapDangMo(
            UUID lopHocPhanId,
            UUID hocVienId
    );

    List<ExerciseStudentsResponseDTO> getBaiTapSapMo(
            UUID lopHocPhanId,
            UUID hocVienId
    );

    List<ExerciseStudentsResponseDTO> getBaiTapDaDong(
            UUID lopHocPhanId,
            UUID hocVienId
    );

    // ================= RESULT =================
    boolean hasExerciseResult(
            UUID exerciseId,
            UUID hocVienId
    );

    // ================= EDIT (FIX CHUẨN) =================
    boolean canEditExercise(
            UUID exerciseId,
            UUID hocVienId
    );
}