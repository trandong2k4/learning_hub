package com.university.service.admin;

import com.university.dto.request.admin.ChuongTrinhDaoTaoAdminRequestDTO;
import com.university.dto.response.admin.ChuongTrinhDaoTaoAdminResponseDTO;
import com.university.entity.MonHoc;
import com.university.entity.Nganh;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.ChuongTrinhDaoTaoAdminMapper;
import com.university.repository.admin.ChuongTrinhDaoTaoAdminRepository;
import com.university.repository.admin.MonHocAdminRepository;
import com.university.repository.admin.NganhAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChuongTrinhDaoTaoAdminService {

    private final ChuongTrinhDaoTaoAdminRepository ctdtRepository;
    private final NganhAdminRepository nganhRepository;
    private final MonHocAdminRepository monHocRepository;
    private final ChuongTrinhDaoTaoAdminMapper ctdtMapper;

    public List<ChuongTrinhDaoTaoAdminResponseDTO> getAll() {
        return ctdtRepository.findAllDTO();
    }

    public List<ChuongTrinhDaoTaoAdminResponseDTO> getByNganhId(UUID nganhId) {
        return ctdtRepository.findAllByNganhIdDTO(nganhId);
    }

    public List<ChuongTrinhDaoTaoAdminResponseDTO> getAllNganh() {
        return nganhRepository.getAllDTO().stream()
                .map(n -> new ChuongTrinhDaoTaoAdminResponseDTO(
                        n.getId(), n.getId(), n.getMaNganh(), n.getTenNganh(),
                        null, null, null, null, null))
                .toList();
    }

    public List<ChuongTrinhDaoTaoAdminResponseDTO> getAllMonHoc() {
        return monHocRepository.FindAllDTO().stream()
                .map(m -> new ChuongTrinhDaoTaoAdminResponseDTO(
                        null, null, null, null,
                        m.getId(), m.getMaMonHoc(), m.getTenMonHoc(), m.getSoTinChi(), m.getMoTa()))
                .toList();
    }

    @Transactional
    public ChuongTrinhDaoTaoAdminResponseDTO create(ChuongTrinhDaoTaoAdminRequestDTO dto) {
        Nganh nganh = nganhRepository.findByMaNganh(dto.getMaNganh())
                .orElseThrow(() -> new SimpleMessageException("Ngành '" + dto.getMaNganh() + "' không tồn tại!"));

        MonHoc monHoc = monHocRepository.findByMaMonHoc(dto.getMaMonHoc())
                .orElseThrow(() -> new SimpleMessageException("Môn học '" + dto.getMaMonHoc() + "' không tồn tại!"));

        if (ctdtRepository.existsByNganh_IdAndMonHoc_Id(nganh.getId(), monHoc.getId())) {
            throw new SimpleMessageException(
                    "Môn học '" + monHoc.getTenMonHoc() + "' đã tồn tại trong chương trình đào tạo của ngành '" + nganh.getTenNganh() + "'!");
        }

        var entity = ctdtMapper.toEntity(dto);
        return ctdtMapper.toResponseDTO(ctdtRepository.save(entity));
    }

    @Transactional
    public List<ChuongTrinhDaoTaoAdminResponseDTO> createList(List<ChuongTrinhDaoTaoAdminRequestDTO> dtos) {
        return dtos.stream()
                .map(this::create)
                .toList();
    }

    @Transactional
    public void delete(UUID id) {
        if (!ctdtRepository.existsById(id)) {
            throw new SimpleMessageException("Chương trình đào tạo không tồn tại!");
        }
        ctdtRepository.deleteById(id);
    }

    @Transactional
    public void deleteList(List<UUID> ids) {
        ctdtRepository.deleteAllByIdIn(ids);
    }
}
