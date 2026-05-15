package com.university.service.student;

import com.university.dto.request.student.ChuongTrinhDaoTaoRequestDTO;
import com.university.dto.response.student.ChuongTrinhDaoTaoResponseDTO;
import com.university.entity.HocVien;
import com.university.exception.students.HocVienChuaGanNganhException;
import com.university.repository.student.ChuongTrinhDaoTaoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChuongTrinhDaoTaoService {

    private final ChuongTrinhDaoTaoRepository chuongTrinhDaoTaoRepository;
    private final CurrentHocVienService currentHocVienService;

    @Transactional(readOnly = true)
    public Page<ChuongTrinhDaoTaoResponseDTO> getDanhSach(ChuongTrinhDaoTaoRequestDTO request, Pageable pageable) {
        HocVien hocVien = currentHocVienService.getCurrentHocVienWithNganh();

        if (hocVien.getNganh() == null || hocVien.getNganh().getId() == null) {
            log.warn("Khong the lay chuong trinh dao tao vi hoc vien chua duoc gan nganh, hocVienId={}", hocVien.getId());
            throw new HocVienChuaGanNganhException("Hoc vien chua duoc gan nganh");
        }

        UUID nganhId = hocVien.getNganh().getId();
        String keyword = chuanHoaKeyword(request);

        if (keyword != null && !keyword.isBlank()) {
            log.debug("Tim kiem chuong trinh dao tao, hocVienId={}, nganhId={}, keywordLength={}, page={}, size={}",
                    hocVien.getId(), nganhId, keyword.length(), pageable.getPageNumber(), pageable.getPageSize());
            return chuongTrinhDaoTaoRepository.findByNganhIdAndKeyword(nganhId, keyword, pageable);
        }

        log.debug("Lay danh sach chuong trinh dao tao, hocVienId={}, nganhId={}, page={}, size={}",
                hocVien.getId(), nganhId, pageable.getPageNumber(), pageable.getPageSize());
        return chuongTrinhDaoTaoRepository.findByNganhId(nganhId, pageable);
    }

    private String chuanHoaKeyword(ChuongTrinhDaoTaoRequestDTO request) {
        if (request == null || request.getKeyword() == null) {
            return null;
        }

        String keyword = request.getKeyword()
                .replaceAll("\\p{Cntrl}", " ")
                .trim()
                .replaceAll("\\s+", " ");

        // Escape ký tự đặc biệt của LIKE để tránh SQL injection qua pattern
        return keyword
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
    }
}
