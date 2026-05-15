package com.university.service.admin;

import com.university.dto.request.admin.LopHocPhanAdminRequestDTO;
import com.university.dto.response.admin.LopHocPhanAdminResponseDTO;
import com.university.entity.HocKi;
import com.university.entity.LopHocPhan;
import com.university.entity.MonHoc;
import com.university.enums.TrangThaiLHP;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.LopHocPhanAdminMapper;
import com.university.repository.admin.HocKiAdminRepository;
import com.university.repository.admin.LopHocPhanAdminRepository;
import com.university.repository.admin.MonHocAdminRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LopHocPhanAdminService {

    private final LopHocPhanAdminRepository lopHocPhanAdminRepository;
    private final HocKiAdminRepository hocKiAdminRepository;
    private final MonHocAdminRepository monHocAdminRepository;
    private final LopHocPhanAdminMapper lopHocPhanAdminMapper;

    @Transactional
    public LopHocPhanAdminResponseDTO create(LopHocPhanAdminRequestDTO request) {
        normalizeRequest(request);

        if (lopHocPhanAdminRepository.existsByMaLopHocPhan(request.getMaLopHocPhan())) {
            throw new SimpleMessageException("Mã lớp học phần đã tồn tại");
        }

        HocKi hocKi = hocKiAdminRepository.findById(request.getHocKiId())
                .orElseThrow(() -> new EntityNotFoundException("Học kì không tồn tại"));
        MonHoc monHoc = monHocAdminRepository.findById(request.getMonHocId())
                .orElseThrow(() -> new EntityNotFoundException("Môn học không tồn tại"));

        LopHocPhan lopHocPhan = lopHocPhanAdminMapper.toEntity(request);
        lopHocPhan.setHocKi(hocKi);
        lopHocPhan.setMonHoc(monHoc);

        LopHocPhan saved = lopHocPhanAdminRepository.save(lopHocPhan);
        return lopHocPhanAdminRepository.findDTOById(saved.getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lớp học phần sau khi tạo"));
    }

    public LopHocPhanAdminResponseDTO getById(UUID id) {
        return lopHocPhanAdminRepository.findDTOById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lớp học phần không tồn tại"));
    }

    public List<LopHocPhanAdminResponseDTO> getAll() {
        return lopHocPhanAdminRepository.findAllDTO();
    }

    public List<LopHocPhanAdminResponseDTO> getAllByHocKi(UUID hocKiId) {
        if (!hocKiAdminRepository.existsById(hocKiId)) {
            throw new EntityNotFoundException("Học kì không tồn tại");
        }
        return lopHocPhanAdminRepository.findAllByHocKiIdDTO(hocKiId);
    }

    public List<LopHocPhanAdminResponseDTO> getAllByMonHoc(UUID monHocId) {
        if (!monHocAdminRepository.existsById(monHocId)) {
            throw new EntityNotFoundException("Môn học không tồn tại");
        }
        return lopHocPhanAdminRepository.findAllByMonHocIdDTO(monHocId);
    }

    @Transactional
    public LopHocPhanAdminResponseDTO update(UUID id, LopHocPhanAdminRequestDTO request) {
        normalizeRequest(request);

        LopHocPhan existing = lopHocPhanAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lớp học phần không tồn tại"));

        if (lopHocPhanAdminRepository.existsByMaLopHocPhanAndIdNot(request.getMaLopHocPhan(), id)) {
            throw new SimpleMessageException("Mã lớp học phần đã tồn tại");
        }

        long soLuongDaDangKy = lopHocPhanAdminRepository.countDangKyByLopHocPhanId(id);
        if (request.getSoLuongToiDa() < soLuongDaDangKy) {
            throw new SimpleMessageException("Số lượng tối đa không được nhỏ hơn số lượng đã đăng ký");
        }

        HocKi hocKi = hocKiAdminRepository.findById(request.getHocKiId())
                .orElseThrow(() -> new EntityNotFoundException("Học kì không tồn tại"));
        MonHoc monHoc = monHocAdminRepository.findById(request.getMonHocId())
                .orElseThrow(() -> new EntityNotFoundException("Môn học không tồn tại"));

        lopHocPhanAdminMapper.updateEntity(existing, request);
        existing.setHocKi(hocKi);
        existing.setMonHoc(monHoc);

        LopHocPhan updated = lopHocPhanAdminRepository.save(existing);
        return lopHocPhanAdminRepository.findDTOById(updated.getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lớp học phần sau khi cập nhật"));
    }

    @Transactional
    public void delete(UUID id) {
        LopHocPhan lopHocPhan = lopHocPhanAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lớp học phần không tồn tại"));

        if (lopHocPhanAdminRepository.countDangKyByLopHocPhanId(id) > 0) {
            throw new SimpleMessageException("Không thể xóa lớp học phần đã có đăng ký tín chỉ");
        }

        lopHocPhanAdminRepository.delete(lopHocPhan);
    }

    @Transactional
    public List<String> deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        List<UUID> deletable = new java.util.ArrayList<>();
        List<String> cannotDelete = new java.util.ArrayList<>();

        for (UUID id : ids) {
            if (lopHocPhanAdminRepository.countDangKyByLopHocPhanId(id) > 0) {
                String maLHP = "Unknown";
                try {
                    maLHP = lopHocPhanAdminRepository.findById(id)
                            .map(LopHocPhan::getMaLopHocPhan)
                            .orElse("Unknown");
                } catch (Exception ignored) {}
                cannotDelete.add(maLHP);
            } else {
                deletable.add(id);
            }
        }

        if (!deletable.isEmpty()) {
            try {
                lopHocPhanAdminRepository.deleteAllByIdIn(deletable);
            } catch (Exception e) {
                throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
            }
        }

        return cannotDelete;
    }

    private void normalizeRequest(LopHocPhanAdminRequestDTO request) {
        if (request == null) {
            throw new SimpleMessageException("Thông tin lớp học phần không được để trống");
        }

        request.setMaLopHocPhan(request.getMaLopHocPhan().trim().toUpperCase());

        if (request.getMaLopHocPhan().length() > 10) {
            throw new SimpleMessageException("Mã lớp học phần tối đa 10 ký tự");
        }

        if (request.getHanHuy().isBefore(request.getHanDangKy())) {
            throw new SimpleMessageException("Hạn hủy không được trước hạn đăng ký");
        }

        if (request.getTrangThai() == null) {
            request.setTrangThai(TrangThaiLHP.MO_DANG_KY);
        }
    }
}
