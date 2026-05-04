package com.university.service.admin;

import com.alibaba.excel.EasyExcel;
import com.university.dto.request.admin.TruongAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.TruongAdminResponseDTO;
import com.university.entity.Truong;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.TruongAdminMapper;
import com.university.repository.admin.TruongAdminRepository;
import com.university.service.admin.excel.TruongExcelListener;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TruongAdminService {

    @Autowired
    private final TruongAdminRepository truongAdminRepository;
    private final TruongAdminMapper truongMapper;

    public ExcelImportResult importFromExcel(MultipartFile file) throws java.io.IOException {
        TruongExcelListener listener = new TruongExcelListener(truongAdminRepository);

        EasyExcel.read(file.getInputStream(), TruongAdminRequestDTO.class, listener)
                .sheet("Truong")
                .headRowNumber(1)
                .doRead();

        return listener.getResult();
    }

    public TruongAdminResponseDTO create(TruongAdminRequestDTO dto) {
        if (truongAdminRepository.existsByMaTruong(dto.getMaTruong())) {
            throw new SimpleMessageException("Mã trường đã tồn tại");
        }
        return truongMapper.toResponseDTO(truongAdminRepository.save(truongMapper.toEntity(dto)));
    }

    public List<TruongAdminResponseDTO> getAll() {
        return truongAdminRepository.FindAllDTO();
    }

    public List<TruongAdminResponseDTO> getByName(String keyword) {
        List<TruongAdminResponseDTO> truong = truongAdminRepository.findTruongByTen(keyword);
        return truong;
    }

    public TruongAdminResponseDTO update(UUID id, TruongAdminRequestDTO dto) {
        Truong truong = truongAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Trường không tồn tại"));
        truongMapper.updateEntity(truong, dto);
        truongAdminRepository.save(truong);
        return truongMapper.toResponseDTO(truong);
    }

    public void delete(UUID id) {
        if (!truongAdminRepository.existsById(id)) {
            throw new SimpleMessageException("Trường không tồn tại");
        }
        truongAdminRepository.deleteById(id);
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
            truongAdminRepository.deleteAllByIdIn(ids);

        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }

}