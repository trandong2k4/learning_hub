package com.university.mapper.admin;

import org.springframework.stereotype.Component;

import com.university.dto.request.admin.NganhAminRequestDTO;
import com.university.dto.response.admin.NganhAdminResponseDTO;
import com.university.entity.Khoa;
import com.university.entity.Nganh;
import com.university.repository.admin.KhoaAdminRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NganhAdminMapper {

    private final KhoaAdminRepository khoaRepository;

    public Nganh toEntity(NganhAminRequestDTO dto) {
        Khoa khoa = khoaRepository.findById(dto.getKhoaId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Khoa"));
        Nganh nganh = new Nganh(null, dto.getMaNganh(), dto.getTenNganh(), dto.getDanhGia(), dto.getMoTa(), khoa, null,
                null);
        return nganh;
    }

    public void upDateEntity(Nganh n, NganhAminRequestDTO dto, Khoa khoa) {
        n.setMaNganh(dto.getMaNganh());
        n.setTenNganh(dto.getTenNganh());
        n.setMoTa(dto.getMoTa());
        n.setDanhGia(dto.getDanhGia());
        n.setKhoa(khoa);
    }

    public NganhAdminResponseDTO toResponseDTO(Nganh entity) {
        NganhAdminResponseDTO n = new NganhAdminResponseDTO();
        n.setId(entity.getId());
        n.setMaNganh(entity.getMaNganh());
        n.setTenNganh(entity.getTenNganh());
        n.setDanhGia(entity.getDanhGia());
        n.setMoTa(entity.getMoTa());
        return n;
    }
}
