package com.university.service.admin;

import com.university.dto.request.admin.ChuongTrinhDaoTaoAdminRequestDTO;
import com.university.dto.response.admin.ChuongTrinhDaoTaoAdminResponseDTO;
import com.university.entity.ChuongTrinhDaoTao;
import com.university.entity.MonHoc;
import com.university.entity.Nganh;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.ChuongTrinhDaoTaoAdminMapper;
import com.university.repository.admin.ChuongTrinhDaoTaoAdminRepository;
import com.university.repository.admin.MonHocAdminRepository;
import com.university.repository.admin.NganhAdminRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChuongTrinhDaoTaoAdminService {

    private final ChuongTrinhDaoTaoAdminRepository chuongTrinhDaoTaoRepository;
    private final NganhAdminRepository nganhRepository;
    private final MonHocAdminRepository monHocAdminRepository;
    private final ChuongTrinhDaoTaoAdminMapper chuongTrinhDaoTaoMapper;

    @Transactional
    public ChuongTrinhDaoTaoAdminResponseDTO createCTDT(ChuongTrinhDaoTaoAdminRequestDTO request) {
        // Kiểm tra Ngành
        Nganh nganh = nganhRepository.findById(request.getNganhId())
                .orElseThrow(() -> new EntityNotFoundException("Ngành học không tồn tại"));

        // Kiểm tra Môn học
        MonHoc monHoc = monHocAdminRepository.findById(request.getMonHocId())
                .orElseThrow(() -> new EntityNotFoundException("Môn học không tồn tại"));

        ChuongTrinhDaoTao ctdt = chuongTrinhDaoTaoMapper.toEntity(request);
        ctdt.setNganh(nganh);
        ctdt.setMonHoc(monHoc);

        ChuongTrinhDaoTao saved = chuongTrinhDaoTaoRepository.save(ctdt);
        return chuongTrinhDaoTaoMapper.toResponseDTO(saved);
    }

    @Transactional
    public List<ChuongTrinhDaoTaoAdminResponseDTO> createListCTDT(List<ChuongTrinhDaoTaoAdminRequestDTO> requests) {

        List<ChuongTrinhDaoTao> list = requests.stream().map(req -> {

            Nganh nganh = nganhRepository.findById(req.getNganhId())
                    .orElseThrow(() -> new EntityNotFoundException("Ngành học không tồn tại"));

            MonHoc monHoc = monHocAdminRepository.findById(req.getMonHocId())
                    .orElseThrow(() -> new EntityNotFoundException("Môn học không tồn tại"));

            ChuongTrinhDaoTao ctdt = chuongTrinhDaoTaoMapper.toEntity(req);
            ctdt.setNganh(nganh);
            ctdt.setMonHoc(monHoc);

            return ctdt;

        }).toList();

        List<ChuongTrinhDaoTao> savedList = chuongTrinhDaoTaoRepository.saveAll(list);

        return savedList.stream()
                .map(chuongTrinhDaoTaoMapper::toResponseDTO)
                .toList();
    }

    public ChuongTrinhDaoTaoAdminResponseDTO getCTDTById(UUID id) {
        return chuongTrinhDaoTaoRepository.findById(id)
                .map(chuongTrinhDaoTaoMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Chương trình đào tạo không tồn tại"));
    }

    public List<ChuongTrinhDaoTaoAdminResponseDTO.ChuongTrinhDaoTaoView> getAllChuongTrinhDaoTao() {
        return chuongTrinhDaoTaoRepository.findAllProjectedBy();
    }

    public List<ChuongTrinhDaoTaoAdminResponseDTO.ChuongTrinhDaoTaoView> getALlChuongTrinhDaoTaoByNganh(UUID nganhId) {
        return chuongTrinhDaoTaoRepository.findAllProjectedByNganh_Id(nganhId);
    }

    @Transactional
    public ChuongTrinhDaoTaoAdminResponseDTO updateCTDT(UUID id, ChuongTrinhDaoTaoAdminRequestDTO request) {
        ChuongTrinhDaoTao existing = chuongTrinhDaoTaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chương trình đào tạo không tồn tại"));

        // Cập nhật Ngành nếu có thay đổi
        if (request.getNganhId() != null) {
            Nganh nganh = nganhRepository.findById(request.getNganhId())
                    .orElseThrow(() -> new EntityNotFoundException("Ngành học không tồn tại"));
            existing.setNganh(nganh);
        }

        // Cập nhật Môn học nếu có thay đổi (Quan trọng: Bạn đang thiếu phần này ở code
        // cũ)
        if (request.getMonHocId() != null) {
            MonHoc monHoc = monHocAdminRepository.findById(request.getMonHocId())
                    .orElseThrow(() -> new EntityNotFoundException("Môn học không tồn tại"));
            existing.setMonHoc(monHoc);
        }

        chuongTrinhDaoTaoMapper.updateEntity(existing, request);

        ChuongTrinhDaoTao updated = chuongTrinhDaoTaoRepository.save(existing);
        return chuongTrinhDaoTaoMapper.toResponseDTO(updated);
    }

    @Transactional
    public void deleteCTDT(UUID id) {
        if (!chuongTrinhDaoTaoRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy chương trình đào tạo để xóa");
        }
        chuongTrinhDaoTaoRepository.deleteById(id);
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
            chuongTrinhDaoTaoRepository.deleteAllByIdIn(ids);

        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}