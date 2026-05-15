package com.university.mapper.admin;

import com.university.dto.request.admin.GiangDayAdminRequestDTO;
import com.university.dto.response.admin.GiangDayAdminResponseDTO;
import com.university.entity.GiangDay;
import com.university.entity.LopHocPhan;
import com.university.entity.MonHoc;
import com.university.entity.NhanVien;
import org.springframework.stereotype.Component;

@Component
public class GiangDayAdminMapper {

    public GiangDay toEntity(GiangDayAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        GiangDay giangDay = new GiangDay();
        giangDay.setVaiTro(dto.getVaiTro());

        return giangDay;
    }

    public void updateEntity(GiangDay giangDay, GiangDayAdminRequestDTO dto) {
        if (dto == null || giangDay == null) {
            return;
        }

        giangDay.setVaiTro(dto.getVaiTro());
    }

    public GiangDayAdminResponseDTO toResponseDTO(GiangDay entity) {
        if (entity == null) {
            return null;
        }

        GiangDayAdminResponseDTO dto = new GiangDayAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setVaiTro(entity.getVaiTro());

        if (entity.getNhanVien() != null) {
            NhanVien nv = entity.getNhanVien();
            dto.setNhanVienId(nv.getId());
            dto.setMaNhanVien(nv.getMaNhanVien());
            if (nv.getUsers() != null) {
                dto.setTenNhanVien(nv.getUsers().getHoTen());
            }
        }

        if (entity.getLopHocPhan() != null) {
            LopHocPhan lhp = entity.getLopHocPhan();
            dto.setLopHocPhanId(lhp.getId());
            dto.setMaLopHocPhan(lhp.getMaLopHocPhan());
            if (lhp.getMonHoc() != null) {
                MonHoc mh = lhp.getMonHoc();
                dto.setTenMonHoc(mh.getTenMonHoc());
                dto.setSoTinChi(mh.getSoTinChi());
            }
            if (lhp.getHocKi() != null) {
                dto.setHocKiId(lhp.getHocKi().getId());
                dto.setMaHocKi(lhp.getHocKi().getMaHocKi());
                dto.setTenHocKi(lhp.getHocKi().getTenHocKi());
            }
        }

        return dto;
    }
}