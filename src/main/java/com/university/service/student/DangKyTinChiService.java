package com.university.service.student;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.dto.request.student.DangKyTinChiRequestDTO;
import com.university.dto.response.student.DangKyTinChiResponseDTO;
import com.university.entity.DangKyTinChi;
import com.university.entity.HocPhi;
import com.university.entity.HocVien;
import com.university.entity.LopHocPhan;
import com.university.enums.HocPhiEnum;
import com.university.repository.admin.HocPhiAdminRepository;
import com.university.repository.student.DangKyTinChiRepository;
import com.university.repository.student.LopHocPhanStudentsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DangKyTinChiService {

    private static final double DON_GIA_TIN_CHI = 700_000.0;

    private final DangKyTinChiRepository dangKyTinChiRepository;
    private final LopHocPhanStudentsRepository lopHocPhanStudentsRepository;
    private final CurrentHocVienService currentHocVienService;
    private final HocPhiAdminRepository hocPhiAdminRepository;

    public DangKyTinChiResponseDTO dangKy(DangKyTinChiRequestDTO request) {
        HocVien hocVien = currentHocVienService.getCurrentHocVienForUpdate();
        UUID hocVienId = hocVien.getId();
        UUID lopHocPhanId = Objects.requireNonNull(request.getLopHocPhanId(), "LopHocPhanId khong duoc null");

        if (dangKyTinChiRepository.existsByHocVienIdAndLopHocPhanId(hocVienId, lopHocPhanId)) {
            throw new IllegalStateException("Hoc vien da dang ky lop hoc phan nay");
        }

        LopHocPhan lopHocPhan = lopHocPhanStudentsRepository.findByIdForUpdate(lopHocPhanId)
                .orElseThrow(() -> new IllegalArgumentException("Lop hoc phan khong ton tai"));

        if (lopHocPhan.getHanDangKy() == null
                || LocalDateTime.now().isAfter(lopHocPhan.getHanDangKy())) {
            throw new IllegalStateException("Da het han dang ky");
        }

        // Kiểm tra trùng lịch và lấy chi tiết slot bị trùng để thông báo cho học viên
        List<Object[]> conflicts = dangKyTinChiRepository.findTrungLichDetails(hocVienId, lopHocPhanId);
        if (!conflicts.isEmpty()) {
            String chiTiet = conflicts.stream()
                    .map(row -> row[0] + " " + row[1] + "–" + row[2] + " (lớp " + row[3] + ")")
                    .collect(Collectors.joining("; "));
            throw new IllegalStateException("Trung lich hoc: " + chiTiet);
        }

        if (dangKyTinChiRepository.daHocMon(hocVienId, lopHocPhan.getMonHoc().getId())) {
            throw new IllegalStateException("Ban da hoc mon nay roi");
        }

        if (!dangKyTinChiRepository.daHocMonTienQuyet(hocVienId, lopHocPhan.getMonHoc().getId())) {
            throw new IllegalStateException("Chua hoc mon tien quyet");
        }

        UUID hocKiId = lopHocPhan.getHocKi().getId();
        boolean laHocKiHe = lopHocPhan.getHocKi().getNgayBatDau() != null
                && lopHocPhan.getHocKi().getNgayBatDau().getMonthValue() >= 6
                && lopHocPhan.getHocKi().getNgayBatDau().getMonthValue() <= 7;
        int maxTinChi = laHocKiHe ? 10 : 25;

        Integer tongTinChiDangKy = dangKyTinChiRepository.sumTinChiByHocVienAndHocKi(hocVienId, hocKiId);
        int tongTinChi = tongTinChiDangKy == null ? 0 : tongTinChiDangKy;
        int tinChiMoi = lopHocPhan.getMonHoc().getSoTinChi();

        if (tongTinChi + tinChiMoi > maxTinChi) {
            throw new IllegalStateException("Vuot qua so tin chi toi da trong hoc ki nay (" + maxTinChi + " TC)");
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

        // Tạo hoặc cập nhật HocPhi cho (hocVien, hocKi)
        int tinChiMoiDangKy = lopHocPhan.getMonHoc().getSoTinChi();
        List<HocPhi> existingHocPhis = hocPhiAdminRepository.findAllByHocVienIdAndHocKiId(hocVienId, hocKiId);

        // Tìm HocPhi chưa thanh toán để cộng dồn tín chỉ
        Optional<HocPhi> unpaidHocPhi = existingHocPhis.stream()
                .filter(hp -> hp.getTrangThai() != HocPhiEnum.DA_THANH_TOAN)
                .findFirst();

        if (unpaidHocPhi.isPresent()) {
            HocPhi hp = unpaidHocPhi.get();
            int updated = (hp.getSoTinChi() == null ? 0 : hp.getSoTinChi()) + tinChiMoiDangKy;
            hp.setSoTinChi(updated);
            hp.setSoTien(updated * DON_GIA_TIN_CHI);
            hocPhiAdminRepository.save(hp);
        } else {
            // Chưa có HocPhi, hoặc tất cả đã DA_THANH_TOAN → tạo HocPhi mới (bổ sung)
            HocPhi hp = new HocPhi();
            hp.setHocVien(hocVien);
            hp.setHocKi(lopHocPhan.getHocKi());
            hp.setSoTinChi(tinChiMoiDangKy);
            hp.setSoTien(tinChiMoiDangKy * DON_GIA_TIN_CHI);
            hp.setTrangThai(HocPhiEnum.CHUA_THANH_TOAN);
            hocPhiAdminRepository.save(hp);
        }

        log.info("Credit registration created: dangKyId={}, hocVienId={}, lopHocPhanId={}",
                dangKy.getId(), hocVienId, lopHocPhanId);

        return dangKyTinChiRepository
                .findDangKyTinChiResponseDTOById(dangKy.getId())
                .orElseThrow(() -> new IllegalStateException("Khong tim thay ket qua"));
    }

    public void huyDangKyTinChi(UUID id) {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();

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

        int tinChiHuy = lopHocPhan.getMonHoc().getSoTinChi();
        UUID hocKiIdHuy = lopHocPhan.getHocKi().getId();

        List<HocPhi> hocPhisHuy = hocPhiAdminRepository.findAllByHocVienIdAndHocKiId(hocVienId, hocKiIdHuy);

        // Tìm HocPhi chưa thanh toán để trừ tín chỉ
        Optional<HocPhi> unpaidHuy = hocPhisHuy.stream()
                .filter(hp -> hp.getTrangThai() != HocPhiEnum.DA_THANH_TOAN)
                .findFirst();

        // Không cho hủy nếu đang chờ xác nhận hoặc tất cả đã thanh toán
        if (unpaidHuy.isPresent() && unpaidHuy.get().getTrangThai() == HocPhiEnum.DANG_XU_LY) {
            throw new IllegalStateException("Không thể hủy đăng ký khi chứng từ đang chờ kế toán xác nhận");
        }
        if (unpaidHuy.isEmpty() && !hocPhisHuy.isEmpty()) {
            throw new IllegalStateException("Không thể hủy đăng ký vì học phí học kỳ này đã được thanh toán");
        }

        dangKyTinChiRepository.delete(dangKyTinChi);

        // Trừ tín chỉ khỏi HocPhi chưa thanh toán
        unpaidHuy.ifPresent(hp -> {
            int updated = Math.max(0, (hp.getSoTinChi() == null ? 0 : hp.getSoTinChi()) - tinChiHuy);
            if (updated == 0) {
                hocPhiAdminRepository.delete(hp);
            } else {
                hp.setSoTinChi(updated);
                hp.setSoTien(updated * DON_GIA_TIN_CHI);
                hocPhiAdminRepository.save(hp);
            }
        });

        log.info("Credit registration canceled: dangKyId={}, hocVienId={}, lopHocPhanId={}",
                dangKyTinChi.getId(), hocVienId, lopHocPhan.getId());
    }

    @Transactional(readOnly = true)
    public List<DangKyTinChiResponseDTO> getDangKyTinChiCuaToi() {
        UUID hocVienId = currentHocVienService.getCurrentHocVienId();
        return dangKyTinChiRepository.findDangKyTinChiResponseDTOByHocVienId(hocVienId);
    }
}
