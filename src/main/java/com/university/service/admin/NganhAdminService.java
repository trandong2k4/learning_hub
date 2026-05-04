package com.university.service.admin;

import com.alibaba.excel.EasyExcel;
import com.university.dto.request.admin.NganhAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.NganhAdminResponseDTO;
import com.university.entity.Khoa;
import com.university.entity.Nganh;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.NganhAdminMapper;
import com.university.repository.admin.KhoaAdminRepository;
import com.university.repository.admin.NganhAdminRepository;
import com.university.service.admin.excel.NganhExcelListener;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NganhAdminService {

    private final NganhAdminRepository nganhRepository;
    private final NganhAdminMapper nganhMapper;
    private final KhoaAdminRepository khoaRepository;

    public NganhAdminResponseDTO create(NganhAdminRequestDTO dto) {
        if (nganhRepository.existsByMaNganh(dto.getMaNganh())) {
            throw new SimpleMessageException("Mã ngành đã tồn tại");
        }
        Khoa khoa = khoaRepository.findByMaKhoa(dto.getMaKhoa());
        if (khoa == null) {
            throw new EntityNotFoundException("Không tìm thấy Khoa");
        }
        Nganh nganh = nganhMapper.toEntity(dto, khoa);
        return nganhMapper.toResponseDTO(nganhRepository.save(nganh));
    }

    public ExcelImportResult importFromExcel(MultipartFile file) throws java.io.IOException {
        NganhExcelListener listener = new NganhExcelListener(khoaRepository, nganhRepository);
        EasyExcel.read(file.getInputStream(), NganhAdminRequestDTO.class, listener)
                .sheet("Nganh")
                .headRowNumber(1)
                .doRead();
        return listener.getResult();
    }

    public List<NganhAdminResponseDTO> getAllNganhResponseDTO() {
        return nganhRepository.getAllDTO();
    }

    public NganhAdminResponseDTO getById(UUID id) {
        Nganh nganh = nganhRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy ngành"));
        return nganhMapper.toResponseDTO(nganh);
    }

    public List<NganhAdminResponseDTO> search(String keyword) {
        return nganhRepository.searchByTenNganh(keyword);
    }

    public NganhAdminResponseDTO update(UUID id, NganhAdminRequestDTO dto) {
        Nganh existing = nganhRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy ngành"));
        Khoa khoa = khoaRepository.findByMaKhoa(dto.getMaKhoa());
        if (khoa == null) {
            throw new EntityNotFoundException("Không tìm thấy khoa với mã khoa: " + dto.getMaKhoa());
        }

        existing.setMaNganh(dto.getMaNganh());
        existing.setTenNganh(dto.getTenNganh());
        existing.setKhoa(khoa);

        return nganhMapper.toResponseDTO(nganhRepository.save(existing));
    }

    public void delete(UUID id) {
        Nganh nganh = nganhRepository.findById(id).orElseThrow();
        if (nganh == null) {
            throw new EntityNotFoundException("Nganh ko ton tai");
        }
        nganhRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            // Kiem tra user dang co trong cac db khac khong
            // for (UUID uuid : ids) {
            // if (usersAdminRepository.) {

            // }
            // }
            nganhRepository.deleteAllByIdIn(ids);

        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}