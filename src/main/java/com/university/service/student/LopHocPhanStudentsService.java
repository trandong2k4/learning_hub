package com.university.service.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.dto.response.student.LopHocPhanStudentsResponseDTO;
import com.university.repository.student.LopHocPhanStudentsRepository;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LopHocPhanStudentsService {

    private final LopHocPhanStudentsRepository lopHocPhanStudentsRepository;

    public Page<LopHocPhanStudentsResponseDTO> searchMoDangKy(UUID hocKiId, String keyword, Pageable pageable) {
        String kw = (keyword == null || keyword.isBlank()) ? "" : keyword.trim();
        return lopHocPhanStudentsRepository.searchMoDangKy(hocKiId, kw, pageable);
    }
}
