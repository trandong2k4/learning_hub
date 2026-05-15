package com.university.service.student;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.university.dto.response.student.ExerciseDetailResponseDTO;
import com.university.dto.response.student.ExerciseStudentsResponseDTO;

public interface ExerciseStudentsService {

    Page<ExerciseStudentsResponseDTO> getDanhSachBaiTap(
            UUID lopHocPhanId,
            String keyword,
            int page,
            int size);

    ExerciseDetailResponseDTO getChiTietBaiTap(UUID exerciseId);

    boolean isExerciseOpen(UUID exerciseId);

    List<ExerciseStudentsResponseDTO> getBaiTapDangMo(UUID lopHocPhanId);

    List<ExerciseStudentsResponseDTO> getBaiTapSapMo(UUID lopHocPhanId);

    List<ExerciseStudentsResponseDTO> getBaiTapDaDong(UUID lopHocPhanId);

    boolean hasExerciseResult(UUID exerciseId);

    boolean canEditExercise(UUID exerciseId);
}
