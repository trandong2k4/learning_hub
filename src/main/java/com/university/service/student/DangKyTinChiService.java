package com.university.service.student;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.university.dto.request.student.DangKyTinChiRequestDTO;
import com.university.dto.response.student.DangKyTinChiResponseDTO;
import com.university.entity.DangKyTinChi;
import com.university.entity.HocVien;
import com.university.entity.LopHocPhan;
import com.university.enums.TrangThaiLHP;
import com.university.repository.admin.HocVienAdminRepository;
import com.university.repository.admin.LopHocPhanAdminRepository;
import com.university.repository.student.DangKyTinChiRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DangKyTinChiService {

    private final DangKyTinChiRepository dangKyTinChiRepository;
    private final HocVienAdminRepository hocVienAdminRepository;
    private final LopHocPhanAdminRepository lopHocPhanAdminRepository;

    // ========================
    // ĐĂNG KÝ TÍN CHỈ
    // ========================
    public synchronized DangKyTinChiResponseDTO dangKy(DangKyTinChiRequestDTO request) {

        // 1. Check đã đăng ký chưa
        if (dangKyTinChiRepository.existsByHocVienIdAndLopHocPhanId(
                request.getHocVienId(), request.getLopHocPhanId())) {
            throw new RuntimeException("Học viên đã đăng ký lớp học phần này");
        }

        // 2. Lấy học viên
        HocVien hocVien = hocVienAdminRepository.findById(request.getHocVienId())
                .orElseThrow(() -> new RuntimeException("Học viên không tồn tại"));

        // 3. Lấy lớp học phần
        LopHocPhan lopHocPhan = lopHocPhanAdminRepository.findById(request.getLopHocPhanId())
                .orElseThrow(() -> new RuntimeException("Lớp học phần không tồn tại"));

        // 4. Check trạng thái
        if (lopHocPhan.getTrangThai() != TrangThaiLHP.MO_DANG_KY) {
            throw new RuntimeException("Lớp học phần không mở đăng ký");
        }

        // 5. Check hạn đăng ký
        if (lopHocPhan.getHanDangKy() != null &&
                LocalDateTime.now().isAfter(lopHocPhan.getHanDangKy())) {
            throw new RuntimeException("Đã hết hạn đăng ký");
        }

        // 6. Check trùng lịch (KHÔNG LOOP)
        boolean trungLich = dangKyTinChiRepository.existsTrungLichFull(
                request.getHocVienId(),
                request.getLopHocPhanId()
        );

        if (trungLich) {
            throw new RuntimeException("Trùng lịch học");
        }

        // 7. Check đã học môn
        if (dangKyTinChiRepository.daHocMon(
                request.getHocVienId(),
                lopHocPhan.getMonHoc().getId())) {
            throw new RuntimeException("Bạn đã học môn này rồi");
        }

        // 8. Check môn tiên quyết
        if (!dangKyTinChiRepository.daHocMonTienQuyet(
                request.getHocVienId(),
                lopHocPhan.getMonHoc().getId())) {
            throw new RuntimeException("Chưa học môn tiên quyết");
        }

        // 9. Check tín chỉ tối đa
        int tongTinChi = dangKyTinChiRepository.sumTinChiByHocVien(request.getHocVienId());
        int tinChiMoi = lopHocPhan.getMonHoc().getSoTinChi();

        if (tongTinChi + tinChiMoi > 25) {
            throw new RuntimeException("Vượt quá số tín chỉ tối đa");
        }

        // 10. Check lớp còn chỗ (DÙNG COUNT DB)
        int soLuong = dangKyTinChiRepository.countByLopHocPhanId(
                request.getLopHocPhanId()
        );

        if (soLuong >= lopHocPhan.getSoLuongToiDa()) {
            throw new RuntimeException("Lớp học phần đã đầy");
        }

        // 11. Lưu đăng ký
        DangKyTinChi dangKy = new DangKyTinChi();
        dangKy.setHocVien(hocVien);
        dangKy.setLopHocPhan(lopHocPhan);

        dangKyTinChiRepository.save(dangKy);

        // 12. Tăng số lượng đã đăng ký
        lopHocPhan.setSoLuongToiDa(lopHocPhan.getSoLuongToiDa() + 1);
        // 13. Trả kết quả
        return dangKyTinChiRepository
                .findDangKyTinChiResponseDTOById(dangKy.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kết quả"));
    }

    // ========================
    // HỦY ĐĂNG KÝ (DÙNG PATH VARIABLE)
    // ========================
    public void huyDangKyTinChi(UUID id) {

        // 1. Tìm đăng ký
        DangKyTinChi dangKyTinChi = dangKyTinChiRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Đăng ký tín chỉ không tồn tại"));

        LopHocPhan lopHocPhan = dangKyTinChi.getLopHocPhan();

        // 2. Check hạn hủy
        if (lopHocPhan.getHanHuy() != null &&
                LocalDateTime.now().isAfter(lopHocPhan.getHanHuy())) {
            throw new RuntimeException("Đã quá hạn hủy đăng ký");
        }

        // 3. Giảm số lượng (AN TOÀN)
        if (lopHocPhan.getSoLuongToiDa() > 0) {
            lopHocPhan.setSoLuongToiDa(lopHocPhan.getSoLuongToiDa() - 1);
        }

        // 4. Xóa
        dangKyTinChiRepository.delete(dangKyTinChi);
    }

    // ========================
    // LẤY DANH SÁCH
    // ========================
    public List<DangKyTinChiResponseDTO> getDangKyTinChiByHocVienId(UUID hocVienId) {
        return dangKyTinChiRepository.findDangKyTinChiResponseDTOByHocVienId(hocVienId);
    }
}