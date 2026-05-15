package com.university.mapper.admin;

import com.university.dto.request.admin.ChuongTrinhDaoTaoAdminRequestDTO;
import com.university.dto.response.admin.ChuongTrinhDaoTaoAdminResponseDTO;
import com.university.entity.ChuongTrinhDaoTao;
import com.university.entity.MonHoc;
import com.university.entity.Nganh;
import com.university.repository.admin.MonHocAdminRepository;
import com.university.repository.admin.NganhAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChuongTrinhDaoTaoAdminMapper {

    private final NganhAdminRepository nganhRepository;
    private final MonHocAdminRepository monHocRepository;

    public ChuongTrinhDaoTao toEntity(ChuongTrinhDaoTaoAdminRequestDTO dto) {
        Nganh nganh = nganhRepository.findByMaNganh(dto.getMaNganh())
                .orElseThrow(() -> new IllegalArgumentException("Ngành không tồn tại: " + dto.getMaNganh()));
        MonHoc monHoc = monHocRepository.findByMaMonHoc(dto.getMaMonHoc())
                .orElseThrow(() -> new IllegalArgumentException("Môn học không tồn tại: " + dto.getMaMonHoc()));

        ChuongTrinhDaoTao entity = new ChuongTrinhDaoTao();
        entity.setNganh(nganh);
        entity.setMonHoc(monHoc);
        return entity;
    }

    public ChuongTrinhDaoTaoAdminResponseDTO toResponseDTO(ChuongTrinhDaoTao entity) {
        return new ChuongTrinhDaoTaoAdminResponseDTO(
                entity.getId(),
                entity.getNganh().getId(),
                entity.getNganh().getMaNganh(),
                entity.getNganh().getTenNganh(),
                entity.getMonHoc().getId(),
                entity.getMonHoc().getMaMonHoc(),
                entity.getMonHoc().getTenMonHoc(),
                entity.getMonHoc().getSoTinChi(),
                entity.getMonHoc().getMoTa()
        );
    }
}
