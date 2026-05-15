package com.university.service.admin;

import com.university.dto.request.admin.MonHocTienQuyetAdminRequestDTO;
import com.university.dto.response.admin.MonHocTienQuyetAdminResponseDTO;
import com.university.entity.MonHoc;
import com.university.entity.MonHocTienQuyet;
import com.university.mapper.admin.MonHocTienQuyetAdminMapper;
import com.university.repository.admin.MonHocAdminRepository;
import com.university.repository.admin.MonHocTienQuyetAdminRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MonHocTienQuyetAdminService {

    private final MonHocTienQuyetAdminRepository monHocTienQuyetAdminRepository;
    private final MonHocAdminRepository monHocAdminRepository;
    private final MonHocTienQuyetAdminMapper monHocTienQuyetMapper;

    @Transactional
    public MonHocTienQuyetAdminResponseDTO create(MonHocTienQuyetAdminRequestDTO request) {
        UUID monHocId = request.getMonHocId();
        UUID monTienQuyetId = request.getMonTienQuyetId();

        if (monHocId.equals(monTienQuyetId)) {
            throw new IllegalArgumentException("Môn học không thể là tiên quyết của chính nó");
        }

        MonHoc monHoc = monHocAdminRepository.findById(monHocId)
                .orElseThrow(() -> new EntityNotFoundException("Môn học chính không tồn tại"));

        MonHoc monTienQuyet = monHocAdminRepository.findById(monTienQuyetId)
                .orElseThrow(() -> new EntityNotFoundException("Môn tiên quyết không tồn tại"));

        if (monHocTienQuyetAdminRepository.existsByMonHocIdAndMonTienQuyetId(monHocId, monTienQuyetId)) {
            throw new IllegalStateException(
                    "'" + monTienQuyet.getMaMonHoc() + "' đã là môn tiên quyết của '" + monHoc.getMaMonHoc() + "'");
        }

        // Kiểm tra vòng tròn trực tiếp: A→B không hợp lệ nếu B→A đã tồn tại
        if (monHocTienQuyetAdminRepository.existsByMonHocIdAndMonTienQuyetId(monTienQuyetId, monHocId)) {
            throw new IllegalStateException(
                    "Xung đột vòng tròn: '" + monHoc.getMaMonHoc() + "' đã là tiên quyết của '"
                            + monTienQuyet.getMaMonHoc() + "'");
        }

        MonHocTienQuyet entity = new MonHocTienQuyet();
        entity.setMonHoc(monHoc);
        entity.setMonTienQuyet(monTienQuyet);
        entity.setMaMonHoc(monHoc.getMaMonHoc());

        return monHocTienQuyetMapper.toResponseDTO(monHocTienQuyetAdminRepository.save(entity));
    }

    @Transactional
    public MonHocTienQuyetAdminResponseDTO update(UUID id, MonHocTienQuyetAdminRequestDTO request) {
        MonHocTienQuyet existing = monHocTienQuyetAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bản ghi tiên quyết không tồn tại"));

        UUID monHocId = request.getMonHocId();
        UUID monTienQuyetId = request.getMonTienQuyetId();

        if (monHocId.equals(monTienQuyetId)) {
            throw new IllegalArgumentException("Môn học không thể là tiên quyết của chính nó");
        }

        MonHoc monHoc = monHocAdminRepository.findById(monHocId)
                .orElseThrow(() -> new EntityNotFoundException("Môn học chính không tồn tại"));

        MonHoc monTienQuyet = monHocAdminRepository.findById(monTienQuyetId)
                .orElseThrow(() -> new EntityNotFoundException("Môn tiên quyết không tồn tại"));

        // Kiểm tra duplicate, bỏ qua bản ghi đang cập nhật
        if (monHocTienQuyetAdminRepository.existsByMonHocIdAndMonTienQuyetIdAndIdNot(monHocId, monTienQuyetId, id)) {
            throw new IllegalStateException(
                    "'" + monTienQuyet.getMaMonHoc() + "' đã là môn tiên quyết của '" + monHoc.getMaMonHoc() + "'");
        }

        // Kiểm tra vòng tròn trực tiếp
        if (monHocTienQuyetAdminRepository.existsByMonHocIdAndMonTienQuyetId(monTienQuyetId, monHocId)) {
            throw new IllegalStateException(
                    "Xung đột vòng tròn: '" + monHoc.getMaMonHoc() + "' đã là tiên quyết của '"
                            + monTienQuyet.getMaMonHoc() + "'");
        }

        existing.setMonHoc(monHoc);
        existing.setMonTienQuyet(monTienQuyet);
        existing.setMaMonHoc(monHoc.getMaMonHoc());

        return monHocTienQuyetMapper.toResponseDTO(monHocTienQuyetAdminRepository.save(existing));
    }

    public MonHocTienQuyetAdminResponseDTO getById(UUID id) {
        MonHocTienQuyet entity = monHocTienQuyetAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bản ghi tiên quyết không tồn tại"));
        return monHocTienQuyetMapper.toResponseDTO(entity);
    }

    public List<MonHocTienQuyetAdminResponseDTO> getAll() {
        return monHocTienQuyetAdminRepository.findAll()
                .stream()
                .map(monHocTienQuyetMapper::toResponseDTO)
                .toList();
    }

    public List<MonHocTienQuyetAdminResponseDTO> getByMonHocId(UUID monHocId) {
        if (!monHocAdminRepository.existsById(monHocId)) {
            throw new EntityNotFoundException("Môn học không tồn tại");
        }
        return monHocTienQuyetAdminRepository.findAllByMonHocId(monHocId)
                .stream()
                .map(monHocTienQuyetMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public void delete(UUID id) {
        if (!monHocTienQuyetAdminRepository.existsById(id)) {
            throw new EntityNotFoundException("Bản ghi tiên quyết không tồn tại");
        }
        monHocTienQuyetAdminRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return;
        monHocTienQuyetAdminRepository.deleteAllByIdIn(ids);
    }
}
