package com.university.service.admin;

import com.alibaba.excel.EasyExcel;
import com.university.dto.request.admin.PhongAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.PhongAdminResponseDTO;
import com.university.entity.Phong;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.PhongAdminMapper;
import com.university.repository.admin.LichAdminRepository;
import com.university.repository.admin.PhongAdminRepository;
import com.university.service.admin.excel.PhongExcelListener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhongAdminService {

    private final PhongAdminRepository phongAdminRepository;
    private final LichAdminRepository lichAdminRepository;
    private final PhongAdminMapper phongAdminMapper;

    public ExcelImportResult importFromExcel(MultipartFile file) throws IOException {
        PhongExcelListener listener = new PhongExcelListener(phongAdminRepository);
        EasyExcel.read(file.getInputStream(), PhongExcelListener.PhongExcelRow.class, listener)
                .sheet("Phong")
                .headRowNumber(1)
                .doRead();
        return listener.getResult();
    }

    private void checkCanDelete(UUID id) {
        if (!phongAdminRepository.existsById(id)) {
            throw new SimpleMessageException("Phòng không tồn tại");
        }
        if (lichAdminRepository.existsByPhongId(id)) {
            throw new SimpleMessageException("Phòng đang được sử dụng trong lịch học, không thể xóa");
        }
    }

    @Transactional
    public PhongAdminResponseDTO create(PhongAdminRequestDTO request) {
        if (phongAdminRepository.findAll().stream()
                .anyMatch(p -> p.getMaPhong().equalsIgnoreCase(request.getMaPhong()))) {
            throw new SimpleMessageException("Mã phòng đã tồn tại");
        }
        Phong phong = phongAdminMapper.toEntity(request);
        return phongAdminMapper.toResponseDTO(phongAdminRepository.save(phong));
    }

    @Transactional
    public String createList(List<PhongAdminRequestDTO> request) {
        if (request == null || request.isEmpty()) {
            return "Danh sách rỗng";
        }
        List<Phong> phongs = request.stream().map(phongAdminMapper::toEntity).toList();
        phongAdminRepository.saveAll(phongs);
        return "Thêm danh sách thành công";
    }

    @Transactional(readOnly = true)
    public PhongAdminResponseDTO getPhongById(UUID id) {
        Phong phong = phongAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Phòng không tồn tại"));
        return phongAdminMapper.toResponseDTO(phong);
    }

    @Transactional(readOnly = true)
    public List<PhongAdminResponseDTO> getAllPhong() {
        return phongAdminRepository.findAllWithLichs().stream()
                .map(phongAdminMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public PhongAdminResponseDTO update(UUID id, PhongAdminRequestDTO request) {
        Phong phong = phongAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Phòng không tồn tại"));
        phong = phongAdminMapper.updateEntity(phong, request);
        phongAdminRepository.save(phong);
        return phongAdminMapper.toResponseDTO(phong);
    }

    @Transactional
    public void delete(UUID id) {
        checkCanDelete(id);
        phongAdminRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty())
            return;
        for (UUID id : ids) {
            checkCanDelete(id);
        }
        try {
            phongAdminRepository.deleteAllByIdIn(ids);
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}
