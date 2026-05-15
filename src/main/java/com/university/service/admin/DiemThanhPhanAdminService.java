package com.university.service.admin;

import com.university.dto.request.admin.DiemThanhPhanAdminRequestDTO;
import com.university.dto.response.admin.DiemThanhPhanAdminResponseDTO;
import com.university.entity.CotDiem;
import com.university.entity.DangKyTinChi;
import com.university.entity.DiemThanhPhan;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.DiemThanhPhanAdminMapper;
import com.university.repository.admin.CotDiemAdminRepository;
import com.university.repository.admin.DangKyTinChiAdminRepository;
import com.university.repository.admin.DiemThanhPhanAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiemThanhPhanAdminService {

    private final DiemThanhPhanAdminRepository diemThanhPhanAdminRepository;
    private final DangKyTinChiAdminRepository dangKyTinChiAdminRepository;
    private final CotDiemAdminRepository cotDiemAdminRepository;
    private final DiemThanhPhanAdminMapper diemThanhPhanMapper;

    @Transactional
    public DiemThanhPhanAdminResponseDTO create(DiemThanhPhanAdminRequestDTO request) {
        if (request == null) {
            throw new SimpleMessageException("Dữ liệu không hợp lệ");
        }

        DangKyTinChi dangKy = dangKyTinChiAdminRepository.findById(request.getDangKyTinChiId())
                .orElseThrow(() -> new SimpleMessageException("Đăng ký tín chỉ không tồn tại"));

        CotDiem cotDiem = cotDiemAdminRepository.findById(request.getCotDiemId())
                .orElseThrow(() -> new SimpleMessageException("Cột điểm không tồn tại"));

        DiemThanhPhan entity = diemThanhPhanMapper.toEntity(request);
        entity.setDangKyTinChi(dangKy);
        entity.setCotDiem(cotDiem);
        entity.setUpdatedAt(LocalDateTime.now());

        DiemThanhPhan saved = diemThanhPhanAdminRepository.save(entity);
        return diemThanhPhanMapper.toResponseDTO(saved);
    }

    @Transactional
    public DiemThanhPhanAdminResponseDTO update(UUID id, DiemThanhPhanAdminRequestDTO request) {
        DiemThanhPhan existing = diemThanhPhanAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Điểm thành phần không tồn tại"));

        DangKyTinChi dangKy = dangKyTinChiAdminRepository.findById(request.getDangKyTinChiId())
                .orElseThrow(() -> new SimpleMessageException("Đăng ký tín chỉ không tồn tại"));

        CotDiem cotDiem = cotDiemAdminRepository.findById(request.getCotDiemId())
                .orElseThrow(() -> new SimpleMessageException("Cột điểm không tồn tại"));

        diemThanhPhanMapper.updateEntity(existing, request);
        existing.setDangKyTinChi(dangKy);
        existing.setCotDiem(cotDiem);
        existing.setUpdatedAt(LocalDateTime.now());

        DiemThanhPhan updated = diemThanhPhanAdminRepository.save(existing);
        return diemThanhPhanMapper.toResponseDTO(updated);
    }

    public DiemThanhPhanAdminResponseDTO getById(UUID id) {
        DiemThanhPhan entity = diemThanhPhanAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Điểm thành phần không tồn tại"));
        return diemThanhPhanMapper.toResponseDTO(entity);
    }

    public List<DiemThanhPhanAdminResponseDTO.DiemThanhPhanView> getAllView() {
        return diemThanhPhanAdminRepository.findAllView();
    }

    @Transactional
    public void delete(UUID id) {
        if (!diemThanhPhanAdminRepository.existsById(id)) {
            throw new SimpleMessageException("Điểm thành phần không tồn tại");
        }
        diemThanhPhanAdminRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return;
        try {
            diemThanhPhanAdminRepository.deleteAllByIdIn(ids);
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}
