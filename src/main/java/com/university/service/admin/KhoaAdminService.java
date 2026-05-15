package com.university.service.admin;

import com.alibaba.excel.EasyExcel;
import com.university.dto.request.admin.KhoaAdminRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.KhoaAdminResponseDTO;
import com.university.entity.Khoa;
import com.university.entity.Nganh;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.KhoaAdminMapper;
import com.university.repository.admin.KhoaAdminRepository;
import com.university.repository.admin.NganhAdminRepository;
import com.university.repository.admin.TruongAdminRepository;
import com.university.service.admin.excel.KhoaExcelListener;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
            // Truong truong = truongRepository.findByMaTruong(dto.getMaTruong());
            // if (truong.equals(null)) {
            // throw new EntityNotFoundException("Trường học không tồn tại");
            // }

            Khoa khoa = khoaMapper.toEntity(dto);
            // khoa.setTruong(truong);

            return khoaMapper.toResponseDTO(khoaRepository.save(khoa));
        } catch (Exception e) {
            throw new BadRequestException("Thêm khoa không thành công!");
        }
    }

    @Transactional
    public List<KhoaAdminResponseDTO> createListKhoa(List<KhoaAdminRequestDTO> requests) {

        List<Khoa> list = requests.stream().map(req -> {

            // Truong truong = truongRepository.findByMaTruong(req.getMaTruong());
            // if (truong.equals(null)) {
            // throw new EntityNotFoundException("Trường học không tồn tại");
            // }

            Khoa khoa = khoaMapper.toEntity(req);
            // khoa.setTruong(truong);
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

        if (!khoa.getMaKhoa().equals(dto.getMaKhoa()) && khoaRepository.existsByMaKhoa(dto.getMaKhoa())) {
            throw new SimpleMessageException("Mã khoa '" + dto.getMaKhoa() + "' đã tồn tại!");
        }

        khoa.setMaKhoa(dto.getMaKhoa());
        khoa.setTenKhoa(dto.getTenKhoa());
        khoa.setDiaChi(dto.getDiaChi());
        khoa.setMoTa(dto.getMoTa());

        // Truong truong = truongRepository.findByMaTruong(dto.getMaTruong());
        // if (truong == null) {
        // throw new EntityNotFoundException("Không tìm thấy trường học");
        // }
        // khoa.setTruong(truong);

        return khoaMapper.toResponseDTO(khoaRepository.save(khoa));
    }

    @Transactional
    public void delete(UUID id) {
        Khoa khoa = khoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Khoa không tồn tại"));

        List<Nganh> nganhs = nganhAdminRepository.findAllByKhoaId(id);
        if (!nganhs.isEmpty()) {
            throw new SimpleMessageException("Khoa '" + khoa.getTenKhoa() + "' vẫn còn " + nganhs.size()
                    + " ngành đang quản lý. Vui lòng xóa các ngành trước.");
        }

        khoaRepository.delete(khoa);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            for (UUID uuid : ids) {
                Khoa khoa = khoaRepository.findById(uuid)
                        .orElseThrow(() -> new EntityNotFoundException("Khoa không tồn tại"));
                List<Nganh> nganhs = nganhAdminRepository.findAllByKhoaId(uuid);
                if (!nganhs.isEmpty()) {
                    throw new SimpleMessageException("Khoa '" + khoa.getTenKhoa() + "' vẫn còn ngành đang quản lý");
                }
            }
            khoaRepository.deleteAllByIdIn(ids);
        } catch (SimpleMessageException e) {
            throw e;
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }

    public ExcelImportResult importFromExcel(MultipartFile file) throws java.io.IOException {
        KhoaExcelListener listener = new KhoaExcelListener(truongRepository, khoaRepository);
        EasyExcel.read(file.getInputStream(), KhoaAdminRequestDTO.class, listener)
                .sheet("Khoa")
                .headRowNumber(1)
                .doRead();
        return listener.getResult();
    }
}
