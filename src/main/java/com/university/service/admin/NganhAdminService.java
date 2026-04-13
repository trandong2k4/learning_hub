package com.university.service.admin;

import com.university.dto.request.admin.NganhAminRequestDTO;
import com.university.dto.response.admin.NganhAdminResponseDTO;
import com.university.entity.Khoa;
import com.university.entity.Nganh;
import com.university.exception.ResourceNotFoundException;
import com.university.mapper.admin.NganhAdminMapper;
import com.university.repository.admin.KhoaAdminRepository;
import com.university.repository.admin.NganhAdminRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NganhAdminService {

    private final NganhAdminRepository nganhRepository;
    private final NganhAdminMapper nganhMapper;
    private final KhoaAdminRepository khoaRepository;

    public NganhAdminResponseDTO create(NganhAminRequestDTO dto) {
        if (nganhRepository.existsByMaNganh(dto.getMaNganh())) {
            throw new RuntimeException("Mã ngành đã tồn tại");
        }
        Nganh nganh = nganhMapper.toEntity(dto);
        return nganhMapper.toResponseDTO(nganhRepository.save(nganh));
    }

    public List<NganhAdminResponseDTO> getAllNganhResponseDTO() {
        return nganhRepository.findAll().stream()
                .map(nganhMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public NganhAdminResponseDTO getById(UUID id) {
        Nganh nganh = nganhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngành"));
        return nganhMapper.toResponseDTO(nganh);
    }

    public List<NganhAdminResponseDTO> search(String keyword) {
        return nganhRepository.searchByTenNganh(keyword).stream()
                .map(nganhMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public NganhAdminResponseDTO update(UUID id, NganhAminRequestDTO dto) {
        Nganh existing = nganhRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngành"));
        Khoa khoa = khoaRepository.findById(dto.getKhoaId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khoa" + dto.getKhoaId()));

        existing.setMaNganh(dto.getMaNganh());
        existing.setTenNganh(dto.getTenNganh());
        existing.setKhoa(khoa);

        return nganhMapper.toResponseDTO(nganhRepository.save(existing));
    }

    public void delete(UUID id) {
        nganhRepository.deleteById(id);
    }
}