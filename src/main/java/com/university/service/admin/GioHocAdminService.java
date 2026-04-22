package com.university.service.admin;

import com.university.dto.request.admin.GioHocAdminRequestDTO;
import com.university.dto.response.admin.GioHocAdminResponseDTO;
import com.university.entity.GioHoc;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.GioHocAdminMapper;
import com.university.repository.admin.GioHocAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GioHocAdminService {

    private final GioHocAdminRepository gioHocRepository;
    private final GioHocAdminMapper gioHocMapper;

    public List<GioHocAdminResponseDTO> getAll() {
        return gioHocRepository.findAllDTO();
    }

    public GioHocAdminResponseDTO getById(UUID id) {
        return gioHocRepository.findDTOById(id);
    }

    public List<GioHocAdminResponseDTO> getByTenGioHoc(String key) {
        return gioHocRepository.searchByTenGioHoc(key);
    }

    @Transactional
    public GioHocAdminResponseDTO create(GioHocAdminRequestDTO dto) {
        if (gioHocRepository.existsByMaGioHoc(dto.getMaGioHoc())) {
            throw new SimpleMessageException("Mã giờ học đã tồn tại");
        }
        // Kiểm tra logic thời gian (Bắt đầu < Kết thúc)
        if (dto.getThoiGianBatDau().isAfter(dto.getThoiGianKetThuc())) {
            throw new SimpleMessageException("Thời gian bắt đầu phải trước thời gian kết thúc");
        }

        GioHoc gioHoc = gioHocMapper.toEntity(dto);
        return gioHocMapper.toResponseDTO(gioHocRepository.save(gioHoc));
    }

    @Transactional
    public GioHocAdminResponseDTO update(UUID id, GioHocAdminRequestDTO dto) {
        GioHoc gioHoc = gioHocRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Giờ học không tồn tại"));

        if (dto.getThoiGianBatDau().isAfter(dto.getThoiGianKetThuc())) {
            throw new SimpleMessageException("Thời gian bắt đầu phải trước thời gian kết thúc");
        }

        gioHocMapper.updateEntity(gioHoc, dto);
        return gioHocMapper.toResponseDTO(gioHocRepository.save(gioHoc));
    }

    @Transactional
    public void delete(UUID id) {
        if (!gioHocRepository.existsById(id)) {
            throw new SimpleMessageException("Giờ học không tồn tại");
        }
        // Lưu ý: Cần kiểm tra ràng buộc với Lịch học trước khi xóa thực tế
        gioHocRepository.deleteById(id);
    }

    @Transactional
    public void deleteMultiple(List<UUID> ids) {
        gioHocRepository.deleteAllByIdInBatch(ids);
    }
}