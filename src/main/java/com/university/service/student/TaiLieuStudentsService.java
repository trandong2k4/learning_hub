package com.university.service.student;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.dto.response.student.TaiLieuStudentsResponseDTO;
import com.university.enums.TaiLieuEnum;
import com.university.exception.students.TaiLieuAccessDeniedException;
import com.university.repository.student.DangKyTinChiRepository;
import com.university.repository.student.TaiLieuStudentsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaiLieuStudentsService {

    private static final int DO_DAI_TOI_DA_KEYWORD = 100;
    private static final int KICH_THUOC_TRANG_TOI_DA = 100;

    private final TaiLieuStudentsRepository taiLieuStudentsRepository;
    private final DangKyTinChiRepository dangKyTinChiRepository;
    private final CurrentHocVienService currentHocVienService;

    @Transactional(readOnly = true)
    public Page<TaiLieuStudentsResponseDTO> getDanhSachTaiLieu(UUID lopHocPhanId, Pageable pageable) {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        kiemTraQuyenTruyCap(hocVienId, lopHocPhanId);
        kiemTraPageable(pageable);

        log.debug("Lay danh sach tai lieu, hocVienId={}, lopHocPhanId={}, page={}, size={}",
                hocVienId, lopHocPhanId, pageable.getPageNumber(), pageable.getPageSize());
        return taiLieuStudentsRepository.findByLopHocPhanId(lopHocPhanId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<TaiLieuStudentsResponseDTO> searchTaiLieu(
            UUID lopHocPhanId, String keyword, String loaiTaiLieu, Pageable pageable) {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        kiemTraQuyenTruyCap(hocVienId, lopHocPhanId);
        kiemTraPageable(pageable);

        String keywordDaChuanHoa = chuanHoaKeyword(keyword);
        TaiLieuEnum loaiTaiLieuDaChuanHoa = chuanHoaLoaiTaiLieu(loaiTaiLieu);

        log.debug("Tim kiem tai lieu, hocVienId={}, lopHocPhanId={}, keywordLength={}, loaiTaiLieu={}, page={}, size={}",
                hocVienId,
                lopHocPhanId,
                keywordDaChuanHoa.length(),
                loaiTaiLieuDaChuanHoa,
                pageable.getPageNumber(),
                pageable.getPageSize());

        return taiLieuStudentsRepository.searchTaiLieu(
                lopHocPhanId,
                keywordDaChuanHoa,
                loaiTaiLieuDaChuanHoa,
                pageable);
    }

    private void kiemTraQuyenTruyCap(UUID hocVienId, UUID lopHocPhanId) {
        if (lopHocPhanId == null) {
            throw new IllegalArgumentException("lopHocPhanId khong duoc null");
        }

        boolean coQuyenTruyCap = dangKyTinChiRepository.existsByHocVienIdAndLopHocPhanId(hocVienId, lopHocPhanId);
        if (!coQuyenTruyCap) {
            log.warn("Hoc vien truy cap tai lieu lop hoc phan khong hop le, hocVienId={}, lopHocPhanId={}",
                    hocVienId, lopHocPhanId);
            throw new TaiLieuAccessDeniedException("Ban khong co quyen truy cap tai lieu cua lop hoc phan nay");
        }
    }

    private void kiemTraPageable(Pageable pageable) {
        if (pageable.getPageSize() > KICH_THUOC_TRANG_TOI_DA) {
            log.warn("Kich thuoc trang tai lieu vuot qua gioi han, size={}", pageable.getPageSize());
            throw new IllegalArgumentException("size khong duoc vuot qua " + KICH_THUOC_TRANG_TOI_DA);
        }
    }

    private String chuanHoaKeyword(String keyword) {
        if (keyword == null) {
            return "";
        }

        String keywordDaChuanHoa = keyword
                .replaceAll("\\p{Cntrl}", " ")
                .trim()
                .replaceAll("\\s+", " ");

        if (keywordDaChuanHoa.isBlank()) {
            return "";
        }

        if (keywordDaChuanHoa.length() > DO_DAI_TOI_DA_KEYWORD) {
            log.warn("Keyword tai lieu vuot qua do dai cho phep, keywordLength={}", keywordDaChuanHoa.length());
            throw new IllegalArgumentException("keyword khong duoc vuot qua " + DO_DAI_TOI_DA_KEYWORD + " ky tu");
        }

        return keywordDaChuanHoa
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
    }

    private TaiLieuEnum chuanHoaLoaiTaiLieu(String loaiTaiLieu) {
        if (loaiTaiLieu == null || loaiTaiLieu.isBlank()) {
            return null;
        }

        try {
            return TaiLieuEnum.valueOf(loaiTaiLieu.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.warn("Loai tai lieu khong hop le, loaiTaiLieu={}", loaiTaiLieu);
            throw new IllegalArgumentException("loaiTaiLieu khong hop le");
        }
    }
}
