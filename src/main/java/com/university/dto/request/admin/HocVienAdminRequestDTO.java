package com.university.dto.request.admin;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HocVienAdminRequestDTO {

    @NotBlank(message = "Mã học viên không được để trống")
    private String maHocVien;

    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime ngayNhapHoc;

    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime ngayTotNghiep;

    @NotBlank(message = "Mã ngành không được để trống")
    private String maNganh;

}