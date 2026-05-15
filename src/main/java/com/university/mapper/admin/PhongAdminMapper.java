package com.university.mapper.admin;

import org.springframework.stereotype.Component;

import com.university.dto.request.admin.PhongAdminRequestDTO;
import com.university.dto.response.admin.PhongAdminResponseDTO;
import com.university.entity.Phong;
import com.university.enums.TinhTrangPhongEnum;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PhongAdminMapper {

    public Phong toEntity(PhongAdminRequestDTO dto) {
        Phong phong = new Phong();
        phong.setMaPhong(dto.getMaPhong());
        phong.setTenPhong(dto.getTenPhong());
        phong.setSucChua(dto.getSucChua());
        phong.setTinhTrang(dto.getTinhTrang());
        phong.setToaNha(dto.getToaNha());
        phong.setTang(dto.getTang());
        return phong;
    }

    public Phong updateEntity(Phong phong, PhongAdminRequestDTO dto) {
        phong.setMaPhong(dto.getMaPhong());
        phong.setTenPhong(dto.getTenPhong());
        phong.setSucChua(dto.getSucChua());
        phong.setTinhTrang(dto.getTinhTrang());
        phong.setToaNha(dto.getToaNha());
        phong.setTang(dto.getTang());
        return phong;
    }

    public PhongAdminResponseDTO toResponseDTO(Phong entity) {
        PhongAdminResponseDTO t = new PhongAdminResponseDTO();
        t.setId(entity.getId());
        t.setMaPhong(entity.getMaPhong());
        t.setTenPhong(entity.getTenPhong());
        t.setSucChua(entity.getSucChua());
        t.setToaNha(entity.getToaNha());
        t.setTang(entity.getTang());

        int soLichHoc = entity.getDLichs() != null ? entity.getDLichs().size() : 0;
        t.setSoLichHoc(soLichHoc);

        // Tu dong xac dinh tinh trang dua tren so lich hoc
        if (soLichHoc > 0) {
            t.setTinhTrang(TinhTrangPhongEnum.DANG_SU_DUNG);
        } else {
            // Neu khong co lich hoc, giu nguyen tinh trang cu neu co, nguoc lai la CHUA_SU_DUNG
            if (entity.getTinhTrang() != null && entity.getTinhTrang() != TinhTrangPhongEnum.KHONG_SU_DUNG_NUA) {
                t.setTinhTrang(entity.getTinhTrang());
            } else {
                t.setTinhTrang(TinhTrangPhongEnum.CHUA_SU_DUNG);
            }
        }

        return t;
    }
}
