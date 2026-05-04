package com.university.service.student;

import com.university.config.SecurityUtils;
import com.university.dto.request.student.ChuongTrinhDaoTaoRequestDTO;
import com.university.dto.response.student.ChuongTrinhDaoTaoResponseDTO;
import com.university.entity.HocVien;
import com.university.exception.ResourceNotFoundException;
import com.university.repository.student.ChuongTrinhDaoTaoRepository;
import com.university.repository.student.HocVienStudentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChuongTrinhDaoTaoService {

    private final ChuongTrinhDaoTaoRepository chuongTrinhDaoTaoRepository;
    private final HocVienStudentsRepository hocVienStudentsRepository;

    public List<ChuongTrinhDaoTaoResponseDTO> getDanhSach(ChuongTrinhDaoTaoRequestDTO request) {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        HocVien hocVien = hocVienStudentsRepository.findByIdWithNganh(hocVienId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay hoc vien"));

        if (hocVien.getNganh() == null || hocVien.getNganh().getId() == null) {
            throw new IllegalStateException("Hoc vien chua duoc gan nganh");
        }

        UUID nganhId = hocVien.getNganh().getId();
        String keyword = request == null || request.getKeyword() == null ? null : request.getKeyword().trim();

        if (keyword != null && !keyword.isBlank()) {
            return chuongTrinhDaoTaoRepository.findByNganhIdAndKeyword(nganhId, keyword);
        }

        return chuongTrinhDaoTaoRepository.findByNganhId(nganhId);
    }
}
