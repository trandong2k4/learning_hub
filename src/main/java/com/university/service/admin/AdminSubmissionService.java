package com.university.service.admin;

import com.university.dto.response.admin.AdminSubmissionResponseDTO;
import com.university.entity.Exercise;
import com.university.entity.HocVien;
import com.university.entity.LopHocPhan;
import com.university.entity.SubmitExercise;
import com.university.entity.Users;
import com.university.repository.admin.AdminSubmissionRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminSubmissionService {

    private final AdminSubmissionRepository submissionRepository;

    @Transactional(readOnly = true)
    public Page<AdminSubmissionResponseDTO> search(String keyword, UUID lopHocPhanId, int page, int size) {
        PageRequest pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "thoiGianNop"));

        return submissionRepository
                .searchSubmissions(trimToNull(keyword), lopHocPhanId, pageable)
                .map(this::toResponse);
    }

    private AdminSubmissionResponseDTO toResponse(SubmitExercise submission) {
        Exercise exercise = submission.getExercise();
        LopHocPhan lopHocPhan = exercise != null ? exercise.getLopHocPhan() : null;
        HocVien hocVien = submission.getHocVien();
        Users user = hocVien != null ? hocVien.getUsers() : null;

        return new AdminSubmissionResponseDTO(
                submission.getId(),
                submission.getPhienThucHien(),
                submission.getFileExerciseUrl(),
                submission.getThoiGianNop(),
                submission.getDiem(),
                submission.getGhiChu(),
                exercise != null ? exercise.getId() : null,
                exercise != null ? exercise.getTieuDe() : null,
                lopHocPhan != null ? lopHocPhan.getId() : null,
                lopHocPhan != null ? lopHocPhan.getMaLopHocPhan() : null,
                lopHocPhan != null && lopHocPhan.getMonHoc() != null ? lopHocPhan.getMonHoc().getTenMonHoc() : null,
                hocVien != null ? hocVien.getId() : null,
                hocVien != null ? hocVien.getMaHocVien() : null,
                user != null ? user.getHoTen() : null);
    }

    private String trimToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
