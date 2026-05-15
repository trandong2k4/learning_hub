package com.university.dto.request.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.TrangThaiLHP;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LopHocPhanAdminRequestDTO {

    @NotBlank(message = "Mã lớp học phần không được để trống")
    private String maLopHocPhan;

    @NotNull(message = "Số lượng tối đa không được để trống")
    @Min(value = 1, message = "Số lượng tối đa phải lớn hơn 0")
    private Integer soLuongToiDa;

    private TrangThaiLHP trangThai;

    @NotNull(message = "Hạn đăng ký không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime hanDangKy;

    @NotNull(message = "Hạn hủy không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime hanHuy;

    @NotNull(message = "Id học kì không được để trống")
    private UUID hocKiId;

    @NotNull(message = "Id môn học không được để trống")
    private UUID monHocId;
}
