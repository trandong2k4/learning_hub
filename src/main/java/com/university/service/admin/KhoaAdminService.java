package com.university.service.admin;

import com.university.dto.request.admin.KhoaAdminRequestDTO;
import com.university.dto.response.admin.KhoaAdminResponseDTO;
import com.university.entity.Khoa;
import com.university.entity.Truong;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.KhoaAdminMapper;
import com.university.repository.admin.KhoaAdminRepository;
import com.university.repository.admin.NganhAdminRepository;
import com.university.repository.admin.TruongAdminRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KhoaAdminService {

    private final KhoaAdminRepository khoaRepository;
    private final TruongAdminRepository truongRepository;
    private final NganhAdminRepository nganhAdminRepository;
    private final KhoaAdminMapper khoaMapper;

    public KhoaAdminResponseDTO createKhoa(KhoaAdminRequestDTO dto) throws BadRequestException {

        if (khoaRepository.existsByMaKhoa(dto.getMaKhoa())) {
            throw new SimpleMessageException("Mã vai trò '" + dto.getMaKhoa() + "' đã tồn tại!");
        }

        try {
            Truong truong = truongRepository.findByMaTruong(dto.getMaTruong());
            if (truong.equals(null)) {
                throw new EntityNotFoundException("Trường học không tồn tại");
            }

            Khoa khoa = khoaMapper.toEntity(dto);
            khoa.setTruong(truong);

            return khoaMapper.toResponseDTO(khoaRepository.save(khoa));
        } catch (Exception e) {
            throw new BadRequestException("Thêm vai trò không thành công!");
        }
    }

    @Transactional
    public List<KhoaAdminResponseDTO> createListKhoa(List<KhoaAdminRequestDTO> requests) {

        List<Khoa> list = requests.stream().map(req -> {

            Truong truong = truongRepository.findByMaTruong(req.getMaTruong());
            if (truong.equals(null)) {
                throw new EntityNotFoundException("Trường học không tồn tại");
            }

            Khoa khoa = khoaMapper.toEntity(req);
            khoa.setTruong(truong);
            return khoa;

        }).toList();

        List<Khoa> savedList = khoaRepository.saveAll(list);

        return savedList.stream()
                .map(khoaMapper::toResponseDTO)
                .toList();
    }

    public KhoaAdminResponseDTO.KhoaView getById(UUID id) {
        return khoaRepository.findAllProjectedById(id);
    }

    public List<KhoaAdminResponseDTO> getAll() {
        return khoaRepository.findAll().stream()
                .map(khoaMapper::toResponseDTO)
                .toList();
    }

    public List<KhoaAdminResponseDTO.KhoaView> getAllKhoaView() {
        return khoaRepository.findAllProjectedBy();
    }

    public List<KhoaAdminResponseDTO> getAllKhoaDTO() {
        return khoaRepository.findAllKhoaDTO();
    }

    public List<KhoaAdminResponseDTO> search(String keyword) {
        return khoaRepository.findByNameKhoaDTO(keyword);
    }

    public KhoaAdminResponseDTO update(UUID id, KhoaAdminRequestDTO dto) {
        Khoa khoa = khoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khoa"));

        khoa.setMaKhoa(dto.getMaKhoa());
        khoa.setTenKhoa(dto.getTenKhoa());
        khoa.setDiaChi(dto.getDiaChi());

        Truong truong = truongRepository.findByMaTruong(dto.getMaTruong());
        if (truong.equals(null)) {
            throw new EntityNotFoundException("Trường học không tồn tại");
        }
        khoa.setTruong(truong);

        return khoaMapper.toResponseDTO(khoaRepository.save(khoa));
    }

    public void delete(UUID id) {
        khoaRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            // Kiem tra khoa dang co trong cac db khac khong
            Integer count = 0;
            for (UUID uuid : ids) {
                if (nganhAdminRepository.existsByKhoaId(uuid)) {
                    count++;
                    throw new SimpleMessageException("Id Khoa " + uuid + " vẫn còn quản lí ngành đào tạo");
                }
            }
            if (count == 0) {

            }
            khoaRepository.deleteAllByIdIn(ids);

        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}