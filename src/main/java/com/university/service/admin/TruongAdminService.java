package com.university.service.admin;

import com.alibaba.excel.EasyExcel;
import com.university.dto.request.admin.TruongAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.TruongAdminResponseDTO;
import com.university.dto.response.admin.TruongAdminResponseDTO.TruongView;
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
    private final TruongAdminRepository truongRepository;
    private final TruongAdminMapper truongMapper;

    public ExcelImportResult importFromExcel(MultipartFile file) throws java.io.IOException {
        TruongExcelListener listener = new TruongExcelListener(truongRepository);

        EasyExcel.read(file.getInputStream(), TruongAdminRequestDTO.class, listener)
                .sheet("Truong")
                .headRowNumber(1)
                .doRead();

        return listener.getResult();
    }

    public TruongAdminResponseDTO create(TruongAdminRequestDTO dto) {
        if (truongRepository.existsByMaTruong(dto.getMaTruong())) {
            throw new SimpleMessageException("Mã trường đã tồn tại");
        }
        return truongMapper.toResponseDTO(truongRepository.save(truongMapper.toEntity(dto)));
    }

    public List<TruongAdminResponseDTO> getAll() {
        return truongRepository.FindAllDTO();
    }

    public TruongAdminResponseDTO getById(UUID id) {
        TruongAdminResponseDTO truong = truongRepository.findTruongById(id);
        return truong;
    }

    public List<TruongAdminResponseDTO> getByName(String keyword) {
        List<TruongAdminResponseDTO> truong = truongRepository.findTruongByTen(keyword);
        return truong;
    }

    public TruongView getViewById(UUID id) {
        TruongView truong = truongRepository.findTruongView(id);
        return truong;
    }

    public TruongAdminResponseDTO update(UUID id, TruongAdminRequestDTO dto) {
        Truong truong = truongRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Trường không tồn tại"));
        truongMapper.updateEntity(truong, dto);
        truongRepository.save(truong);
        return truongMapper.toResponseDTO(truong);
    }

    public void delete(UUID id) {
        if (!truongRepository.existsById(id)) {
            throw new SimpleMessageException("Trường không tồn tại");
        }
        truongRepository.deleteById(id);
    }

    @Transactional
    public void deleteMultiple(List<UUID> ids) {
        // 2. Xóa hàng loạt
        truongRepository.deleteAllByIdInBatch(ids);
    }

    @Transactional
    public void deleteAll() {
        truongRepository.deleteAll();
    }

}