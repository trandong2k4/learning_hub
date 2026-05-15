package com.university.service.admin;

import com.university.dto.request.admin.CotDiemAdminRequestDTO;
import com.university.dto.response.admin.CotDiemAdminResponseDTO;
import com.university.entity.CotDiem;
import com.university.entity.LopHocPhan;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.CotDiemAdminMapper;
import com.university.repository.admin.CotDiemAdminRepository;
import com.university.repository.admin.LopHocPhanAdminRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CotDiemAdminService {

    private final CotDiemAdminRepository cotDiemAdminRepository;
    private final LopHocPhanAdminRepository lopHocPhanAdminRepository;
    private final CotDiemAdminMapper cotDiemAdminMapper;

    @Transactional
    public CotDiemAdminResponseDTO create(CotDiemAdminRequestDTO request) {
        normalizeRequest(request);

        LopHocPhan lopHocPhan = lopHocPhanAdminRepository.findById(request.getLopHocPhanId())
                .orElseThrow(() -> new EntityNotFoundException("Lớp học phần không tồn tại"));

        if (cotDiemAdminRepository.existsByLopHocPhan_IdAndTenCotDiemIgnoreCase(
                lopHocPhan.getId(), request.getTenCotDiem())) {
            throw new SimpleMessageException("Tên cột điểm đã tồn tại trong lớp học phần này");
        }

        CotDiem cotDiem = cotDiemAdminMapper.toEntity(request);
        cotDiem.setLopHocPhan(lopHocPhan);

        return cotDiemAdminMapper.toResponseDTO(cotDiemAdminRepository.save(cotDiem));
    }

    public CotDiemAdminResponseDTO getById(UUID id) {
        CotDiem cotDiem = cotDiemAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cột điểm không tồn tại"));
        return cotDiemAdminMapper.toResponseDTO(cotDiem);
    }

    public List<CotDiemAdminResponseDTO> getAll() {
        return cotDiemAdminRepository.findAllDTO();
    }

    public List<CotDiemAdminResponseDTO> getAllByLopHocPhan(UUID lopHocPhanId) {
        if (!lopHocPhanAdminRepository.existsById(lopHocPhanId)) {
            throw new EntityNotFoundException("Lớp học phần không tồn tại");
        }
        return cotDiemAdminRepository.findAllByLopHocPhanIdDTO(lopHocPhanId);
    }

    @Transactional
    public CotDiemAdminResponseDTO update(UUID id, CotDiemAdminRequestDTO request) {
        normalizeRequest(request);

        CotDiem existing = cotDiemAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cột điểm không tồn tại"));

        LopHocPhan lopHocPhan = lopHocPhanAdminRepository.findById(request.getLopHocPhanId())
                .orElseThrow(() -> new EntityNotFoundException("Lớp học phần không tồn tại"));

        if (cotDiemAdminRepository.existsByLopHocPhan_IdAndTenCotDiemIgnoreCaseAndIdNot(
                lopHocPhan.getId(), request.getTenCotDiem(), id)) {
            throw new SimpleMessageException("Tên cột điểm đã tồn tại trong lớp học phần này");
        }

        cotDiemAdminMapper.updateEntity(existing, request);
        existing.setLopHocPhan(lopHocPhan);

        return cotDiemAdminMapper.toResponseDTO(cotDiemAdminRepository.save(existing));
    }

    @Transactional
    public void delete(UUID id) {
        CotDiem cotDiem = cotDiemAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cột điểm không tồn tại"));
        cotDiemAdminRepository.delete(cotDiem);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            cotDiemAdminRepository.deleteAllByIdIn(ids);
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }

    private void normalizeRequest(CotDiemAdminRequestDTO request) {
        if (request == null) {
            throw new SimpleMessageException("Thông tin cột điểm không được để trống");
        }

        request.setTenCotDiem(request.getTenCotDiem().trim());
        request.setTiTrong(request.getTiTrong().trim());

        if (request.getTenCotDiem().length() > 10) {
            throw new SimpleMessageException("Tên cột điểm tối đa 10 ký tự");
        }

        if (request.getTiTrong().length() > 10) {
            throw new SimpleMessageException("Tỉ trọng tối đa 10 ký tự");
        }
    }
}
