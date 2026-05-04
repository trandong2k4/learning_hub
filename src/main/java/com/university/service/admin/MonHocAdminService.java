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

    public MonHocAdminResponseDTO updateMonHoc(UUID id, MonHocAdminRequestDTO request) {
        MonHoc monHoc = monHocAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Môn học không tồn tại"));
        monHoc = monHocAdminMapper.updateEntity(monHoc, request);
        monHocAdminRepository.save(monHoc);
        return monHocAdminMapper.toResponseDTO(monHoc);
    }

    public void delete(UUID monhocId) {
        monHocAdminRepository.deleteById(monhocId);
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
            monHocAdminRepository.deleteAllByIdIn(ids);

        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }

}