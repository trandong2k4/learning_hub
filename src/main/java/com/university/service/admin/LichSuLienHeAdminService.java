package com.university.service.admin;

import com.university.dto.request.admin.LichSuLienHeAdminRequestDTO;
import com.university.dto.response.admin.LichSuLienHeAdminResponseDTO;
import com.university.entity.LichSuLienHe;
import com.university.entity.LienHe;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.LichSuLienHeAdminMapper;
import com.university.repository.admin.LichSuLienHeAdminRepository;
import com.university.repository.admin.LienHeAdminRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LichSuLienHeAdminService {

    private final LichSuLienHeAdminRepository lichSuLienHeAdminRepository;
    private final LienHeAdminRepository lienHeAdminRepository;
    private final LichSuLienHeAdminMapper lichSuLienHeAdminMapper;

    @Transactional
    public LichSuLienHeAdminResponseDTO create(LichSuLienHeAdminRequestDTO request) {
        normalizeRequest(request);

        LienHe lienHe = lienHeAdminRepository.findById(request.getLienHeId())
                .orElseThrow(() -> new EntityNotFoundException("Liên hệ không tồn tại"));

        LichSuLienHe lichSuLienHe = lichSuLienHeAdminMapper.toEntity(request);
        lichSuLienHe.setLienHe(lienHe);

        LichSuLienHe saved = lichSuLienHeAdminRepository.save(lichSuLienHe);
        return lichSuLienHeAdminMapper.toResponseDTO(saved, lienHe);
    }

    public LichSuLienHeAdminResponseDTO getById(UUID id) {
        LichSuLienHe lichSuLienHe = lichSuLienHeAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lịch sử liên hệ không tồn tại"));
        LienHe lienHe = lienHeAdminRepository.findById(lichSuLienHe.getLienHe().getId())
                .orElseThrow(() -> new EntityNotFoundException("Liên hệ không tồn tại"));
        return lichSuLienHeAdminMapper.toResponseDTO(lichSuLienHe, lienHe);
    }

    public List<LichSuLienHeAdminResponseDTO> getAll() {
        return lichSuLienHeAdminRepository.findAllDTO();
    }

    public List<LichSuLienHeAdminResponseDTO> getAllByLienHe(UUID lienHeId) {
        if (!lienHeAdminRepository.existsById(lienHeId)) {
            throw new EntityNotFoundException("Liên hệ không tồn tại");
        }
        return lichSuLienHeAdminRepository.findAllByLienHeIdDTO(lienHeId);
    }

    public List<LichSuLienHeAdminResponseDTO> search(String keyword) {
        String cleanedKeyword = keyword == null ? "" : keyword.trim();
        if (cleanedKeyword.isBlank()) {
            return getAll();
        }
        return lichSuLienHeAdminRepository.searchDTO(cleanedKeyword);
    }

    @Transactional
    public LichSuLienHeAdminResponseDTO update(UUID id, LichSuLienHeAdminRequestDTO request) {
        normalizeRequest(request);

        LichSuLienHe existing = lichSuLienHeAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lịch sử liên hệ không tồn tại"));

        LienHe lienHe = lienHeAdminRepository.findById(request.getLienHeId())
                .orElseThrow(() -> new EntityNotFoundException("Liên hệ không tồn tại"));

        lichSuLienHeAdminMapper.updateEntity(existing, request);
        existing.setLienHe(lienHe);

        return lichSuLienHeAdminMapper.toResponseDTO(lichSuLienHeAdminRepository.save(existing), lienHe);
    }

    @Transactional
    public void delete(UUID id) {
        LichSuLienHe lichSuLienHe = lichSuLienHeAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lịch sử liên hệ không tồn tại"));
        lichSuLienHeAdminRepository.delete(lichSuLienHe);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            lichSuLienHeAdminRepository.deleteAllByIdIn(ids);
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }

    private void normalizeRequest(LichSuLienHeAdminRequestDTO request) {
        if (request == null) {
            throw new SimpleMessageException("Thông tin lịch sử liên hệ không được để trống");
        }

        request.setNguoiLienHe(request.getNguoiLienHe().trim());
        request.setEmail(request.getEmail().trim().toLowerCase());

        if (request.getSoDienThoai() != null) {
            request.setSoDienThoai(request.getSoDienThoai().trim());
            if (request.getSoDienThoai().isBlank()) {
                request.setSoDienThoai(null);
            }
        }

        if (request.getNguoiLienHe().length() > 30) {
            throw new SimpleMessageException("Người liên hệ tối đa 30 ký tự");
        }

        if (request.getEmail().length() > 50) {
            throw new SimpleMessageException("Email tối đa 50 ký tự");
        }
    }
}
