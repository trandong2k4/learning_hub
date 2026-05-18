package com.university.service.admin;

import com.university.dto.request.admin.DangKyTinChiAdminRequestDTO;
import com.university.dto.response.admin.DangKyTinChiAdminResponseDTO;
import com.university.entity.DangKyTinChi;
import com.university.entity.HocVien;
import com.university.entity.LopHocPhan;
import com.university.enums.TrangThaiLHP;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.DangKyTinChiAdminMapper;
import com.university.repository.admin.DangKyTinChiAdminRepository;
import com.university.repository.admin.HocKiAdminRepository;
import com.university.repository.admin.HocVienAdminRepository;
import com.university.repository.admin.LopHocPhanAdminRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DangKyTinChiAdminService {

    private static final int MAX_TIN_CHI = 25;

    private final DangKyTinChiAdminRepository dangKyTinChiAdminRepository;
    private final HocKiAdminRepository hocKiAdminRepository;
    private final HocVienAdminRepository hocVienAdminRepository;
    private final LopHocPhanAdminRepository lopHocPhanAdminRepository;
    private final DangKyTinChiAdminMapper dangKyTinChiAdminMapper;

    @Transactional
    public DangKyTinChiAdminResponseDTO create(DangKyTinChiAdminRequestDTO request) {
        HocVien hocVien = hocVienAdminRepository.findById(request.getHocVienId())
                .orElseThrow(() -> new EntityNotFoundException("Học viên không tồn tại"));

        LopHocPhan lopHocPhan = lopHocPhanAdminRepository.findById(request.getLopHocPhanId())
                .orElseThrow(() -> new EntityNotFoundException("Lớp học phần không tồn tại"));

        validateRegistration(hocVien.getId(), lopHocPhan, null, null);

        DangKyTinChi dangKyTinChi = dangKyTinChiAdminMapper.toEntity(request);
        dangKyTinChi.setHocVien(hocVien);
        dangKyTinChi.setLopHocPhan(lopHocPhan);

        try {
            DangKyTinChi saved = dangKyTinChiAdminRepository.saveAndFlush(dangKyTinChi);
            return dangKyTinChiAdminRepository.findByIdWithDetails(saved.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Khong tim thay dang ky tin chi sau khi tao"));
        } catch (DataIntegrityViolationException e) {
            throw new SimpleMessageException("Học viên đã đăng ký lớp học phần này");
        }
    }

    @Transactional(readOnly = true)
    public DangKyTinChiAdminResponseDTO getById(UUID id) {
        return dangKyTinChiAdminRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Đăng ký tín chỉ không tồn tại"));
    }

    @Transactional(readOnly = true)
    public List<DangKyTinChiAdminResponseDTO> getAll() {
        return dangKyTinChiAdminRepository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    public List<DangKyTinChiAdminResponseDTO> getAllByHocVien(UUID hocVienId) {
        if (!hocVienAdminRepository.existsById(hocVienId)) {
            throw new EntityNotFoundException("Học viên không tồn tại");
        }
        return dangKyTinChiAdminRepository.findAllByHocVienIdWithDetails(hocVienId);
    }

    @Transactional(readOnly = true)
    public List<DangKyTinChiAdminResponseDTO> getAllByLopHocPhan(UUID lopHocPhanId) {
        if (!lopHocPhanAdminRepository.existsById(lopHocPhanId)) {
            throw new EntityNotFoundException("Lớp học phần không tồn tại");
        }
        return dangKyTinChiAdminRepository.findAllByLopHocPhanIdWithDetails(lopHocPhanId);
    }

    @Transactional(readOnly = true)
    public List<DangKyTinChiAdminResponseDTO> getAllByHocKi(UUID hocKiId) {
        if (!hocKiAdminRepository.existsById(hocKiId)) {
            throw new EntityNotFoundException("Học kì không tồn tại");
        }
        return dangKyTinChiAdminRepository.findAllByHocKiIdWithDetails(hocKiId);
    }

    @Transactional(readOnly = true)
    public List<DangKyTinChiAdminResponseDTO> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return dangKyTinChiAdminRepository.findAllWithDetails();
        }
        return dangKyTinChiAdminRepository.searchByKeyword(keyword.trim());
    }

    @Transactional(readOnly = true)
    public List<DangKyTinChiAdminResponseDTO> getAllDetail() {
        return dangKyTinChiAdminRepository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    public List<DangKyTinChiAdminResponseDTO.LopHocPhanDangKyView> getLopHocPhanMoDangKy() {
        return dangKyTinChiAdminRepository.findLopHocPhanMoDangKy();
    }

    @Transactional(readOnly = true)
    public List<DangKyTinChiAdminResponseDTO.HocVienDangKyView> getHocVienDangKy() {
        return dangKyTinChiAdminRepository.findHocVienDangKy();
    }

    @Transactional
    public DangKyTinChiAdminResponseDTO update(UUID id, DangKyTinChiAdminRequestDTO request) {
        DangKyTinChi existing = dangKyTinChiAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Đăng ký tín chỉ không tồn tại"));

        HocVien hocVien = hocVienAdminRepository.findById(request.getHocVienId())
                .orElseThrow(() -> new EntityNotFoundException("Học viên không tồn tại"));

        LopHocPhan lopHocPhan = lopHocPhanAdminRepository.findById(request.getLopHocPhanId())
                .orElseThrow(() -> new EntityNotFoundException("Lớp học phần không tồn tại"));

        UUID existingLopHocPhanId = existing.getLopHocPhan().getId();
        validateRegistration(hocVien.getId(), lopHocPhan, id, existingLopHocPhanId);

        existing.setHocVien(hocVien);
        existing.setLopHocPhan(lopHocPhan);
        dangKyTinChiAdminMapper.updateEntity(existing, request);

        try {
            DangKyTinChi updated = dangKyTinChiAdminRepository.saveAndFlush(existing);
            return dangKyTinChiAdminRepository.findByIdWithDetails(updated.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đăng ký tín chỉ sau khi cập nhật"));
        } catch (DataIntegrityViolationException e) {
            throw new SimpleMessageException("Học viên đã đăng ký lớp học phần này");
        }
    }

    @Transactional
    public void delete(UUID id) {
        if (!dangKyTinChiAdminRepository.existsById(id)) {
            throw new EntityNotFoundException("Đăng ký tín chỉ không tồn tại");
        }
        dangKyTinChiAdminRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        long found = dangKyTinChiAdminRepository.countByIdIn(ids);
        if (found < ids.size()) {
            throw new SimpleMessageException("Một số đăng ký tín chỉ không tồn tại");
        }
        dangKyTinChiAdminRepository.deleteAllByIdIn(ids);
    }

    private void validateRegistration(UUID hocVienId, LopHocPhan lopHocPhan, UUID excludeId, UUID existingLopHocPhanId) {
        UUID lopHocPhanId = lopHocPhan.getId();
        UUID monHocId = lopHocPhan.getMonHoc().getId();

        boolean duplicated = excludeId == null
                ? dangKyTinChiAdminRepository.existsByHocVien_IdAndLopHocPhan_Id(hocVienId, lopHocPhanId)
                : dangKyTinChiAdminRepository.existsByHocVien_IdAndLopHocPhan_IdAndIdNot(
                        hocVienId, lopHocPhanId, excludeId);
        if (duplicated) {
            throw new SimpleMessageException("Học viên đã đăng ký lớp học phần này");
        }

        if (lopHocPhan.getTrangThai() != TrangThaiLHP.MO_DANG_KY) {
            throw new SimpleMessageException("Lớp học phần không mở đăng ký");
        }

        if (lopHocPhan.getHanDangKy() != null && LocalDateTime.now().isAfter(lopHocPhan.getHanDangKy())) {
            throw new SimpleMessageException("Đã hết hạn đăng ký");
        }

        boolean trungLich = excludeId == null
                ? dangKyTinChiAdminRepository.existsTrungLichFull(hocVienId, lopHocPhanId)
                : dangKyTinChiAdminRepository.existsTrungLichFullExcludingId(hocVienId, lopHocPhanId, excludeId);
        if (trungLich) {
            throw new SimpleMessageException("Trùng lịch học");
        }

        boolean daHocMon = excludeId == null
                ? dangKyTinChiAdminRepository.daHocMon(hocVienId, monHocId)
                : dangKyTinChiAdminRepository.daHocMonExcludingId(hocVienId, monHocId, excludeId);
        if (daHocMon) {
            throw new SimpleMessageException("Học viên đã học môn này trong học kì này");
        }

        if (!dangKyTinChiAdminRepository.daHocMonTienQuyet(hocVienId, monHocId)) {
            throw new SimpleMessageException("Học viên chưa học môn tiên quyết");
        }

        Integer tongTinChiDangKy = excludeId == null
                ? dangKyTinChiAdminRepository.sumTinChiByHocVien(hocVienId)
                : dangKyTinChiAdminRepository.sumTinChiByHocVienExcludingId(hocVienId, excludeId);
        int tongTinChi = tongTinChiDangKy == null ? 0 : tongTinChiDangKy;
        int tinChiMoi = lopHocPhan.getMonHoc().getSoTinChi() == null ? 0 : lopHocPhan.getMonHoc().getSoTinChi();
        if (tongTinChi + tinChiMoi > MAX_TIN_CHI) {
            throw new SimpleMessageException("Vượt quá số tín chỉ tối đa (" + MAX_TIN_CHI + " tín chỉ)");
        }

        int soLuongDangKy = dangKyTinChiAdminRepository.countByLopHocPhan_Id(lopHocPhanId);
        boolean sameRegistrationClass = excludeId != null && lopHocPhanId.equals(existingLopHocPhanId);
        if (!sameRegistrationClass && lopHocPhan.getSoLuongToiDa() != null && soLuongDangKy >= lopHocPhan.getSoLuongToiDa()) {
            throw new SimpleMessageException("Lớp học phần đã đầy");
        }
    }
}
