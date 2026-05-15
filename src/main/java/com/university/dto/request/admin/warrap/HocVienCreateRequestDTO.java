package com.university.dto.request.admin.warrap;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HocVienCreateRequestDTO {

    @NotBlank(message = "Mã học viên không được để trống")
    private String maHocVien;

    @NotBlank(message = "Mã ngành không được để trống")
    private String maNganh;

    @NotBlank(message = "Vui lòng chọn tài khoản người dùng")
    private String usersId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime ngayNhapHoc;
}
