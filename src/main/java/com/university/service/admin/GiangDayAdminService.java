package com.university.service.admin;

import com.university.dto.request.admin.GiangDayAdminRequestDTO;
import com.university.dto.response.admin.GiangDayAdminResponseDTO;
import com.university.entity.GiangDay;
import com.university.entity.Lich;
import com.university.entity.NhanVien;
import com.university.entity.LopHocPhan;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.GiangDayAdminMapper;
import com.university.repository.admin.GiangDayAdminRepository;
import com.university.repository.admin.LichAdminRepository;
import com.university.repository.admin.NhanVienAdminRepository;
import com.university.repository.admin.LopHocPhanAdminRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GiangDayAdminService {

    private final GiangDayAdminRepository giangDayAdminRepository;
    private final NhanVienAdminRepository nhanVienRepository;
    private final LopHocPhanAdminRepository lopHocPhanRepository;
    private final LichAdminRepository lichRepository;
    private final GiangDayAdminMapper giangDayMapper;

    private boolean isLecturer(NhanVien nhanVien) {
        if (nhanVien.getUsers() == null) {
            return false;
        }
        return nhanVien.getUsers().getDUserRoles().stream()
                .anyMatch(ur -> "LECTURER".equals(ur.getRole().getMaRole()));
    }

    private void checkScheduleConflict(UUID nhanVienId, UUID newLopHocPhanId) {
        List<Lich> lecturerSchedules = lichRepository.findAllByNhanVienId(nhanVienId);
        List<Lich> newLichs = lichRepository.findAllLichByLopHocPhanId(newLopHocPhanId);

        for (Lich newLich : newLichs) {
            for (Lich existingLich : lecturerSchedules) {
                if (existingLich.getLopHocPhan().getId().equals(newLopHocPhanId)) {
                    continue;
                }

                if (isScheduleConflict(existingLich, newLich)) {
                    String existingClass = existingLich.getLopHocPhan().getMaLopHocPhan();
                    String newClass = newLich.getLopHocPhan().getMaLopHocPhan();
                    String ngayHoc = newLich.getNgayHoc() != null
                            ? newLich.getNgayHoc().toLocalDate().toString()
                            : "N/A";
                    String gioHoc = newLich.getGioHoc() != null
                            ? newLich.getGioHoc().getTenGioHoc()
                            : "N/A";

                    throw new SimpleMessageException(String.format(
                            "Xung đột lịch giảng dạy! Giảng viên đã có lớp '%s' vào cùng thời gian với lớp '%s' (Ngày: %s, Giờ: %s)",
                            existingClass, newClass, ngayHoc, gioHoc));
                }
            }
        }
    }

    private boolean isScheduleConflict(Lich lich1, Lich lich2) {
        if (lich1.getNgayHoc() == null || lich2.getNgayHoc() == null) {
            return false;
        }

        LocalDateTime ngay1 = lich1.getNgayHoc();
        LocalDateTime ngay2 = lich2.getNgayHoc();

        if (!ngay1.toLocalDate().equals(ngay2.toLocalDate())) {
            return false;
        }

        if (lich1.getGioHoc() == null || lich2.getGioHoc() == null) {
            return false;
        }

        LocalDateTime start1 = ngay1.with(lich1.getGioHoc().getThoiGianBatDau());
        LocalDateTime end1 = ngay1.with(lich1.getGioHoc().getThoiGianKetThuc());
        LocalDateTime start2 = ngay2.with(lich2.getGioHoc().getThoiGianBatDau());
        LocalDateTime end2 = ngay2.with(lich2.getGioHoc().getThoiGianKetThuc());

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    @Transactional
    public GiangDayAdminResponseDTO createGiangDay(GiangDayAdminRequestDTO request) {
        NhanVien nhanVien = nhanVienRepository.findById(request.getNhanVienId())
                .orElseThrow(() -> new EntityNotFoundException("Nhân viên không tồn tại"));

        if (!isLecturer(nhanVien)) {
            throw new SimpleMessageException(
                    "Chỉ nhân viên có vai trò Giảng viên (LECTURER) mới được phân công giảng dạy");
        }

        if (giangDayAdminRepository.existsByNhanVienIdAndLopHocPhanId(request.getNhanVienId(),
                request.getLopHocPhanId())) {
            throw new SimpleMessageException("Nhân viên này đã được phân công lớp học phần này");
        }

        LopHocPhan lopHocPhan = lopHocPhanRepository.findById(request.getLopHocPhanId())
                .orElseThrow(() -> new EntityNotFoundException("Lớp học phần không tồn tại"));

        checkScheduleConflict(request.getNhanVienId(), request.getLopHocPhanId());

        GiangDay giangDay = giangDayMapper.toEntity(request);
        giangDay.setNhanVien(nhanVien);
        giangDay.setLopHocPhan(lopHocPhan);

        GiangDay saved = giangDayAdminRepository.save(giangDay);
        return giangDayMapper.toResponseDTO(saved);
    }

    public GiangDayAdminResponseDTO getGiangDayById(UUID id) {
        GiangDay giangDay = giangDayAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Giảng dạy không tồn tại"));
        return giangDayMapper.toResponseDTO(giangDay);
    }

    public List<GiangDayAdminResponseDTO> getAll() {
        List<GiangDay> giangDays = giangDayAdminRepository.findAllWithDetails();
        return giangDays.stream()
                .map(giangDayMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public GiangDayAdminResponseDTO updateGiangDay(UUID id, GiangDayAdminRequestDTO request) {
        GiangDay existing = giangDayAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Giảng dạy không tồn tại"));

        UUID currentNhanVienId = existing.getNhanVien().getId();
        UUID currentLopHocPhanId = existing.getLopHocPhan().getId();
        UUID newNhanVienId = request.getNhanVienId() != null ? request.getNhanVienId() : currentNhanVienId;
        UUID newLopHocPhanId = request.getLopHocPhanId() != null ? request.getLopHocPhanId() : currentLopHocPhanId;

        if (request.getNhanVienId() != null && !request.getNhanVienId().equals(currentNhanVienId)) {
            NhanVien nhanVien = nhanVienRepository.findById(request.getNhanVienId())
                    .orElseThrow(() -> new EntityNotFoundException("Nhân viên không tồn tại"));

            if (!isLecturer(nhanVien)) {
                throw new SimpleMessageException(
                        "Chỉ nhân viên có vai trò Giảng viên (LECTURER) mới được phân công giảng dạy");
            }
            existing.setNhanVien(nhanVien);
        }

        if (request.getLopHocPhanId() != null && !request.getLopHocPhanId().equals(currentLopHocPhanId)) {
            if (giangDayAdminRepository.existsByNhanVienIdAndLopHocPhanId(newNhanVienId,
                    request.getLopHocPhanId())) {
                throw new SimpleMessageException("Nhân viên này đã được phân công lớp học phần này");
            }
            LopHocPhan lopHocPhan = lopHocPhanRepository.findById(request.getLopHocPhanId())
                    .orElseThrow(() -> new EntityNotFoundException("Lớp học phần không tồn tại"));
            existing.setLopHocPhan(lopHocPhan);
        }

        if (!newNhanVienId.equals(currentNhanVienId) || !newLopHocPhanId.equals(currentLopHocPhanId)) {
            checkScheduleConflict(newNhanVienId, newLopHocPhanId);
        }

        giangDayMapper.updateEntity(existing, request);
        GiangDay updated = giangDayAdminRepository.save(existing);
        return giangDayMapper.toResponseDTO(updated);
    }

    @Transactional
    public void deleteGiangDay(UUID id) {
        GiangDay gd = giangDayAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Giảng dạy không tồn tại"));
        giangDayAdminRepository.delete(gd);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            giangDayAdminRepository.deleteAllByIdIn(ids);
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}
