package com.university.service.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.StringUtils;
import com.university.dto.request.admin.MonHocAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.MonHocAdminResponseDTO;
import com.university.entity.MonHoc;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.MonHocAdminMapper;
import com.university.repository.admin.MonHocAdminRepository;
import com.university.service.admin.excel.MonHocExcelListener;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MonHocAdminService {
    private final MonHocAdminMapper monHocAdminMapper;
    private final MonHocAdminRepository monHocAdminRepository;

    public ExcelImportResult importFromExcel(MultipartFile file) throws java.io.IOException {
        MonHocExcelListener listener = new MonHocExcelListener(monHocAdminRepository);

        EasyExcel.read(file.getInputStream(), MonHocAdminRequestDTO.class, listener)
                .sheet("MonHoc")
                .headRowNumber(1)
                .doRead();

        return listener.getResult();
    }

    public List<MonHocAdminResponseDTO> getAllMonHoc() {
        List<MonHoc> list = monHocAdminRepository.findAll();
        return list.stream().map(monHoc -> {
            MonHocAdminResponseDTO dto = new MonHocAdminResponseDTO();
            dto.setId(monHoc.getId());
            dto.setMaMonHoc(monHoc.getMaMonHoc());
            dto.setTenMonHoc(monHoc.getTenMonHoc());
            dto.setSoTinChi(monHoc.getSoTinChi());
            dto.setMoTa(monHoc.getMoTa());
            return dto;
        }).toList();
    }

    public MonHocAdminResponseDTO create(MonHocAdminRequestDTO dto) {
        try {
            if (StringUtils.isBlank(dto.getMaMonHoc())) {
                throw new SimpleMessageException("Mã môn học không được để trống");
            }
            if (StringUtils.isBlank(dto.getTenMonHoc())) {
                throw new SimpleMessageException("Tên môn học không được để trống");
            }

            if (monHocAdminRepository.existsByMaMonHoc(dto.getMaMonHoc()))
                throw new SimpleMessageException("Mã quyền '" + dto.getMaMonHoc() + "' đã tồn tại!");

            return monHocAdminMapper.toResponseDTO(monHocAdminRepository.save(monHocAdminMapper.toEntity(dto)));

        } catch (Exception e) {
            throw new SimpleMessageException("Thêm môn học không thành công!");
        }
    }

    @Transactional
    public void updateMonHoc(UUID id, MonHocAdminRequestDTO request) {
        MonHoc monHoc = monHocAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Môn học không tồn tại"));
        monHocAdminMapper.updateEntity(monHoc, request);
        monHocAdminRepository.save(monHoc);
    }

    public List<MonHocAdminResponseDTO> getALLMonHOCDTO() {
        return monHocAdminRepository.FindAllDTO();
    }

    public MonHocAdminResponseDTO getMonHocById(UUID id) {
        MonHocAdminResponseDTO monHoc = monHocAdminRepository.findMonHocById(id);
        if (monHoc == null) {
            throw new EntityNotFoundException("Không tìm thấy môn học");
        }
        return monHoc;
    }

    public List<MonHocAdminResponseDTO> getMonHocByName(String keyword) {
        return monHocAdminRepository.findMonHocByTen(keyword);
    }

    public MonHocAdminResponseDTO update(UUID id, MonHocAdminRequestDTO dto) {
        MonHoc monHoc = monHocAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khoa"));

        monHoc.setMaMonHoc(dto.getMaMonHoc());
        monHoc.setTenMonHoc(dto.getTenMonHoc());

        return monHocAdminMapper.toResponseDTO(monHocAdminRepository.save(monHoc));
    }

    public void delete(UUID monhocId) {
        monHocAdminRepository.deleteById(monhocId);
    }

    public void deleteAllByList(List<UUID> ids) {
        for (UUID id : ids) {
            delete(id);
        }
    }

    @Transactional
    public void deleteAll() {
        monHocAdminRepository.deleteAll();
    }

}