package com.university.service.student;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.config.SecurityUtils;
import com.university.dto.request.student.DangKyTinChiRequestDTO;
import com.university.dto.response.student.DangKyTinChiResponseDTO;
import com.university.entity.DangKyTinChi;
import com.university.entity.HocVien;
import com.university.entity.LopHocPhan;
import com.university.enums.TrangThaiLHP;
import com.university.repository.student.DangKyTinChiRepository;
import com.university.repository.student.HocVienStudentsRepository;
import com.university.repository.student.LopHocPhanStudentsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DangKyTinChiService {

    private final DangKyTinChiRepository dangKyTinChiRepository;
    private final HocVienStudentsRepository hocVienStudentsRepository;
    private final LopHocPhanStudentsRepository lopHocPhanStudentsRepository;

    public DangKyTinChiResponseDTO dangKy(DangKyTinChiRequestDTO request) {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        UUID lopHocPhanId = Objects.requireNonNull(request.getLopHocPhanId(), "LopHocPhanId khong duoc null");

        if (dangKyTinChiRepository.existsByHocVienIdAndLopHocPhanId(hocVienId, lopHocPhanId)) {
            throw new IllegalStateException("Hoc vien da dang ky lop hoc phan nay");
        }

        HocVien hocVien = hocVienStudentsRepository.findByIdForUpdate(hocVienId)
                .orElseThrow(() -> new IllegalArgumentException("Hoc vien khong ton tai"));

        LopHocPhan lopHocPhan = lopHocPhanStudentsRepository.findByIdForUpdate(lopHocPhanId)
                .orElseThrow(() -> new IllegalArgumentException("Lop hoc phan khong ton tai"));

        if (lopHocPhan.getTrangThai() != TrangThaiLHP.MO_DANG_KY) {
            throw new IllegalStateException("Lop hoc phan khong mo dang ky");
        }

        if (lopHocPhan.getHanDangKy() != null
                && LocalDateTime.now().isAfter(lopHocPhan.getHanDangKy())) {
            throw new IllegalStateException("Da het han dang ky");
        }

        if (dangKyTinChiRepository.existsTrungLichFull(hocVienId, lopHocPhanId)) {
            throw new IllegalStateException("Trung lich hoc");
        }

        if (dangKyTinChiRepository.daHocMon(hocVienId, lopHocPhan.getMonHoc().getId())) {
            throw new IllegalStateException("Ban da hoc mon nay roi");
        }

        if (!dangKyTinChiRepository.daHocMonTienQuyet(hocVienId, lopHocPhan.getMonHoc().getId())) {
            throw new IllegalStateException("Chua hoc mon tien quyet");
        }

        Integer tongTinChiDangKy = dangKyTinChiRepository.sumTinChiByHocVien(hocVienId);
        int tongTinChi = tongTinChiDangKy == null ? 0 : tongTinChiDangKy;
        int tinChiMoi = lopHocPhan.getMonHoc().getSoTinChi();

        if (tongTinChi + tinChiMoi > 25) {
            throw new IllegalStateException("Vuot qua so tin chi toi da");
        }

        int soLuong = dangKyTinChiRepository.countByLopHocPhanId(lopHocPhanId);
        if (soLuong >= lopHocPhan.getSoLuongToiDa()) {
            throw new IllegalStateException("Lop hoc phan da day");
        }

        DangKyTinChi dangKy = new DangKyTinChi();
        dangKy.setHocVien(hocVien);
        dangKy.setLopHocPhan(lopHocPhan);

        try {
            dangKyTinChiRepository.saveAndFlush(dangKy);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Duplicate credit registration blocked: hocVienId={}, lopHocPhanId={}", hocVienId, lopHocPhanId);
            throw new IllegalStateException("Hoc vien da dang ky lop hoc phan nay");
        }

        log.info("Credit registration created: dangKyId={}, hocVienId={}, lopHocPhanId={}",
                dangKy.getId(), hocVienId, lopHocPhanId);

        return dangKyTinChiRepository
                .findDangKyTinChiResponseDTOById(dangKy.getId())
                .orElseThrow(() -> new IllegalStateException("Khong tim thay ket qua"));
    }

    public void huyDangKyTinChi(UUID id) {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();

        DangKyTinChi dangKyTinChi = dangKyTinChiRepository
                .findById(Objects.requireNonNull(id, "Id khong duoc null"))
                .orElseThrow(() -> new IllegalArgumentException("Dang ky tin chi khong ton tai"));

        if (!dangKyTinChi.getHocVien().getId().equals(hocVienId)) {
            throw new IllegalStateException("Khong duoc huy dang ky cua hoc vien khac");
        }

        LopHocPhan lopHocPhan = dangKyTinChi.getLopHocPhan();

        if (lopHocPhan.getHanHuy() != null
                && LocalDateTime.now().isAfter(lopHocPhan.getHanHuy())) {
            throw new IllegalStateException("Da qua han huy dang ky");
        }

        dangKyTinChiRepository.delete(dangKyTinChi);
        log.info("Credit registration canceled: dangKyId={}, hocVienId={}, lopHocPhanId={}",
                dangKyTinChi.getId(), hocVienId, lopHocPhan.getId());
    }

    @Transactional(readOnly = true)
    public List<DangKyTinChiResponseDTO> getDangKyTinChiCuaToi() {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        return dangKyTinChiRepository.findDangKyTinChiResponseDTOByHocVienId(hocVienId);
    }
}
