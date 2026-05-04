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

    private final GioHocAdminRepository gioHocAdminRepository;
    private final GioHocAdminMapper gioHocMapper;

    public List<GioHocAdminResponseDTO> getAll() {
        return gioHocAdminRepository.findAllDTO();
    }

    public GioHocAdminResponseDTO getById(UUID id) {
        return gioHocAdminRepository.findDTOById(id);
    }

    public List<GioHocAdminResponseDTO> getByTenGioHoc(String key) {
        return gioHocAdminRepository.searchByTenGioHoc(key);
    }

    @Transactional
    public GioHocAdminResponseDTO create(GioHocAdminRequestDTO dto) {
        if (gioHocAdminRepository.existsByMaGioHoc(dto.getMaGioHoc())) {
            throw new SimpleMessageException("Mã giờ học đã tồn tại");
        }
        // Kiểm tra logic thời gian (Bắt đầu < Kết thúc)
        if (dto.getThoiGianBatDau().isAfter(dto.getThoiGianKetThuc())) {
            throw new SimpleMessageException("Thời gian bắt đầu phải trước thời gian kết thúc");
        }

        GioHoc gioHoc = gioHocMapper.toEntity(dto);
        return gioHocMapper.toResponseDTO(gioHocAdminRepository.save(gioHoc));
    }

    @Transactional
    public GioHocAdminResponseDTO update(UUID id, GioHocAdminRequestDTO dto) {
        GioHoc gioHoc = gioHocAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Giờ học không tồn tại"));

        if (dto.getThoiGianBatDau().isAfter(dto.getThoiGianKetThuc())) {
            throw new SimpleMessageException("Thời gian bắt đầu phải trước thời gian kết thúc");
        }

        gioHocMapper.updateEntity(gioHoc, dto);
        return gioHocMapper.toResponseDTO(gioHocAdminRepository.save(gioHoc));
    }

    @Transactional
    public void delete(UUID id) {
        if (!gioHocAdminRepository.existsById(id)) {
            throw new SimpleMessageException("Giờ học không tồn tại");
        }
        // Lưu ý: Cần kiểm tra ràng buộc với Lịch học trước khi xóa thực tế
        gioHocAdminRepository.deleteById(id);
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
            gioHocAdminRepository.deleteAllByIdIn(ids);

        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}