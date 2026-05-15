package com.university.service.admin;

import com.university.dto.request.admin.LienHeAdminRequestDTO;
import com.university.dto.response.admin.LienHeAdminResponseDTO;
import com.university.entity.Khoa;
import com.university.entity.LienHe;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.LienHeAdminMapper;
import com.university.repository.admin.KhoaAdminRepository;
import com.university.repository.admin.LienHeAdminRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LienHeAdminService {

    private final LienHeAdminRepository lienHeAdminRepository;
    private final KhoaAdminRepository khoaAdminRepository;
    private final LienHeAdminMapper lienHeAdminMapper;

    @Transactional
    public LienHeAdminResponseDTO create(LienHeAdminRequestDTO request) {
        normalizeRequest(request);
        validateUnique(request, null);

        Khoa khoa = khoaAdminRepository.findById(request.getKhoaId())
                .orElseThrow(() -> new EntityNotFoundException("Khoa không tồn tại"));

        LienHe lienHe = lienHeAdminMapper.toEntity(request);
        lienHe.setKhoa(khoa);

        LienHe saved = lienHeAdminRepository.save(lienHe);
        return lienHeAdminMapper.toResponseDTO(saved, khoa);
    }

    public LienHeAdminResponseDTO getById(UUID id) {
        LienHe lienHe = lienHeAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Liên hệ không tồn tại"));
        Khoa khoa = khoaAdminRepository.findById(lienHe.getKhoa().getId())
                .orElseThrow(() -> new EntityNotFoundException("Khoa không tồn tại"));
        return lienHeAdminMapper.toResponseDTO(lienHe, khoa);
    }

    public List<LienHeAdminResponseDTO> getAll() {
        return lienHeAdminRepository.findAllDTO();
    }

    public List<LienHeAdminResponseDTO> getAllByKhoa(UUID khoaId) {
        if (!khoaAdminRepository.existsById(khoaId)) {
            throw new EntityNotFoundException("Khoa không tồn tại");
        }
        return lienHeAdminRepository.findAllByKhoaIdDTO(khoaId);
    }

    public List<LienHeAdminResponseDTO> search(String keyword) {
        String cleanedKeyword = keyword == null ? "" : keyword.trim();
        if (cleanedKeyword.isBlank()) {
            return getAll();
        }
        return lienHeAdminRepository.searchDTO(cleanedKeyword);
    }

    @Transactional
    public LienHeAdminResponseDTO update(UUID id, LienHeAdminRequestDTO request) {
        normalizeRequest(request);

        LienHe existing = lienHeAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Liên hệ không tồn tại"));

        validateUnique(request, id);

        Khoa khoa = khoaAdminRepository.findById(request.getKhoaId())
                .orElseThrow(() -> new EntityNotFoundException("Khoa không tồn tại"));

        lienHeAdminMapper.updateEntity(existing, request);
        existing.setKhoa(khoa);

        return lienHeAdminMapper.toResponseDTO(lienHeAdminRepository.save(existing), khoa);
    }

    @Transactional
    public void delete(UUID id) {
        LienHe lienHe = lienHeAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Liên hệ không tồn tại"));
        lienHeAdminRepository.delete(lienHe);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            lienHeAdminRepository.deleteAllByIdIn(ids);
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }

    private void normalizeRequest(LienHeAdminRequestDTO request) {
        if (request == null) {
            throw new SimpleMessageException("Thông tin liên hệ không được để trống");
        }

        request.setTenLienHe(request.getTenLienHe().trim());
        request.setFanPageUrl(request.getFanPageUrl().trim());
        request.setEmail(request.getEmail().trim().toLowerCase());

        if (request.getSoDienThoai() != null) {
            request.setSoDienThoai(request.getSoDienThoai().trim());
            if (request.getSoDienThoai().isBlank()) {
                request.setSoDienThoai(null);
            }
        }

        if (request.getTenLienHe().length() > 30) {
            throw new SimpleMessageException("Tên liên hệ tối đa 30 ký tự");
        }

        if (request.getEmail().length() > 50) {
            throw new SimpleMessageException("Email tối đa 50 ký tự");
        }
    }

    private void validateUnique(LienHeAdminRequestDTO request, UUID currentId) {
        boolean tenLienHeExists = currentId == null
                ? lienHeAdminRepository.existsByTenLienHeIgnoreCase(request.getTenLienHe())
                : lienHeAdminRepository.existsByTenLienHeIgnoreCaseAndIdNot(request.getTenLienHe(), currentId);
        if (tenLienHeExists) {
            throw new SimpleMessageException("Tên liên hệ đã tồn tại");
        }

        boolean emailExists = currentId == null
                ? lienHeAdminRepository.existsByEmailIgnoreCase(request.getEmail())
                : lienHeAdminRepository.existsByEmailIgnoreCaseAndIdNot(request.getEmail(), currentId);
        if (emailExists) {
            throw new SimpleMessageException("Email liên hệ đã tồn tại");
        }

        if (request.getSoDienThoai() != null) {
            boolean phoneExists = currentId == null
                    ? lienHeAdminRepository.existsBySoDienThoai(request.getSoDienThoai())
                    : lienHeAdminRepository.existsBySoDienThoaiAndIdNot(request.getSoDienThoai(), currentId);
            if (phoneExists) {
                throw new SimpleMessageException("Số điện thoại liên hệ đã tồn tại");
            }
        }
    }
}
