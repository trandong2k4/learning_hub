package com.university.dto.response.admin;

import java.util.UUID;

import com.university.entity.HocKi;
import com.university.entity.MonHoc;
import com.university.enums.TrangThaiLHP;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LopHocPhanAdminResponseDTO {

    private UUID id;
    private String maLopHocPhan;
    private Integer soLuongToiDa;
    private TrangThaiLHP trangThai;
    private HocKi hocKiId;
    private MonHoc monHocId;

    public interface LopHocPhanView {
        UUID getId();
    }
}