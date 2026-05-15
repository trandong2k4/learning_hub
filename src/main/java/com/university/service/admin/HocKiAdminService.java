package com.university.service.admin;

import com.university.dto.request.admin.HocKiAdminRequestDTO;
import com.university.dto.response.admin.HocKiAdminResponseDTO;
import com.university.entity.HocKi;
import com.university.entity.LopHocPhan;
import com.university.entity.HocPhi;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.HocKiAdminMapper;
import com.university.repository.admin.HocKiAdminRepository;
import com.university.repository.admin.LopHocPhanAdminRepository;
import com.university.repository.admin.HocPhiAdminRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HocKiAdminService {

    private final HocKiAdminRepository hocKiAdminRepository;
    private final HocKiAdminMapper hocKiMapper;
    private final LopHocPhanAdminRepository lopHocPhanAdminRepository;
    private final HocPhiAdminRepository hocPhiAdminRepository;

    @Transactional
    public HocKiAdminResponseDTO createHocKi(HocKiAdminRequestDTO request) {
        if (hocKiAdminRepository.existsByMaHocKi(request.getMaHocKi())) {
            throw new SimpleMessageException("Mã học kì đã tồn tại");
        }

        if (request.getNgayBatDau() != null && request.getNgayKetThuc() != null
                && request.getNgayBatDau().isAfter(request.getNgayKetThuc())) {
            throw new SimpleMessageException("Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
        }

        HocKi entity = hocKiMapper.toEntity(request);
        HocKi saved = hocKiAdminRepository.save(entity);
        return hocKiMapper.toResponseDTO(saved);
    }

    @Transactional
    public HocKiAdminResponseDTO updateHocKi(UUID id, HocKiAdminRequestDTO request) {
        HocKi existing = hocKiAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Học kì không tồn tại"));

        if (!existing.getMaHocKi().equals(request.getMaHocKi())
                && hocKiAdminRepository.existsByMaHocKi(request.getMaHocKi())) {
            throw new SimpleMessageException("Mã học kì đã tồn tại");
        }

        if (request.getNgayBatDau() != null && request.getNgayKetThuc() != null
                && request.getNgayBatDau().isAfter(request.getNgayKetThuc())) {
            throw new SimpleMessageException("Ngày bắt đầu phải nhỏ hơn ngày kết thúc");
        }

        hocKiMapper.updateEntity(existing, request);
        HocKi updated = hocKiAdminRepository.save(existing);
        return hocKiMapper.toResponseDTO(updated);
    }

    public HocKiAdminResponseDTO getHocKiById(UUID id) {
        HocKi hocKi = hocKiAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Học kì không tồn tại"));
        return hocKiMapper.toResponseDTO(hocKi);
    }

    public List<HocKiAdminResponseDTO.HocKiView> getAllHocKi() {
        return hocKiAdminRepository.findAllProjectedBy();
    }

    @Transactional
    public void deleteHocKi(UUID id) {
        HocKi hk = hocKiAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Học kì không tồn tại"));

        List<LopHocPhan> lopHocPhans = lopHocPhanAdminRepository.findAllByHocKiId(id);
        if (!lopHocPhans.isEmpty()) {
            lopHocPhanAdminRepository.deleteAll(lopHocPhans);
        }

        List<HocPhi> hocPhis = hocPhiAdminRepository.findAllByHocKiId(id);
        if (!hocPhis.isEmpty()) {
            hocPhiAdminRepository.deleteAll(hocPhis);
        }

        hocKiAdminRepository.delete(hk);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            for (UUID id : ids) {
                deleteHocKi(id);
            }
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}
