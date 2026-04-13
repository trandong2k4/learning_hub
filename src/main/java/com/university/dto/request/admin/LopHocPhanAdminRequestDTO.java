package com.university.dto.request.admin;

import com.university.entity.HocKi;
import com.university.entity.MonHoc;
import com.university.enums.TrangThaiLHP;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LopHocPhanAdminRequestDTO {

    @NotBlank(message = "Tên lên hệ không được để trống")
    private String maLopHocPhan;
    private Integer soLuongToiDa;
    private TrangThaiLHP trangThai;
    @NotNull(message = "Id học kì không được để trống")
    private HocKi hocKiId;
    @NotNull(message = "Id môn học không được để trống")
    private MonHoc monHocId;

}