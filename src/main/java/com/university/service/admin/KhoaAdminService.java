package com.university.service.admin;

import com.university.dto.request.admin.KhoaAminRequestDTO;
import com.university.dto.response.admin.KhoaAdminResponseDTO;
import com.university.entity.Khoa;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.KhoaAdminMapper;
import com.university.repository.admin.KhoaAdminRepository;
import com.university.repository.admin.TruongAdminRepository;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KhoaAdminService {

    private final KhoaAdminRepository khoaRepository;
    private final TruongAdminRepository truongRepository;
    private final KhoaAdminMapper khoaMapper;

    public KhoaAdminResponseDTO create(KhoaAminRequestDTO dto) {
        if (StringUtils.isBlank(dto.getMaKhoa())) {
            throw new SimpleMessageException("Mã khoa không được để trống");
        }

        if (StringUtils.isBlank(dto.getTenKhoa())) {
            throw new SimpleMessageException("Tên khoa không được để trống");
        }

        if (dto.getTruongId().equals(null)) {
            throw new SimpleMessageException("TrườngId không được để trống");
        }

        if (khoaRepository.existsByMaKhoa(dto.getMaKhoa()))
            throw new SimpleMessageException("Mã vai trò '" + dto.getMaKhoa() + "' đã tồn tại!");

        try {
            var truong = truongRepository.findById(dto.getTruongId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy trường"));
            var khoa = khoaMapper.toEntity(dto, truong);

            return khoaMapper.toResponseDTO(khoaRepository.save(khoa));
        } catch (Exception e) {
            // log.error("Lỗi khi tạo role: ", e);
            throw new RuntimeException("Thêm vai trò không thành công!");
        }
    }

    public KhoaAdminResponseDTO getById(UUID id) {
        return khoaRepository.findByIdKhoaDTO(id);
    }

    @Transactional(readOnly = true)
    public List<KhoaAdminResponseDTO> getAll() {
        return khoaRepository.findAll().stream()
                .map(khoaMapper::toResponseDTO)
                .toList();
    }

    public List<KhoaAdminResponseDTO> getAllKhoaDTO() {
        return khoaRepository.findAllKhoaDTO();
    }

    public List<KhoaAdminResponseDTO> search(String keyword) {
        return khoaRepository.findByNameKhoaDTO(keyword);
    }

    public KhoaAdminResponseDTO update(UUID id, KhoaAminRequestDTO dto) {
        Khoa khoa = khoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khoa"));

        khoa.setMaKhoa(dto.getMaKhoa());
        khoa.setTenKhoa(dto.getTenKhoa());

        var truong = truongRepository.findById(dto.getTruongId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy trường"));
        khoa.setTruong(truong);

        return khoaMapper.toResponseDTO(khoaRepository.save(khoa));
    }

    public void delete(UUID id) {
        khoaRepository.deleteById(id);
    }
}