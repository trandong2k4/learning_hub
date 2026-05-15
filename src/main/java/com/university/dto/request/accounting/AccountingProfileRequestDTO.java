package com.university.dto.request.accounting;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.GioiTinhEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountingProfileRequestDTO {

    @NotBlank(message = "Họ tên không được để trống")
    private String hoTen;

    private String diaChi;

    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải có 10 chữ số")
    private String soDienThoai;

    private GioiTinhEnum gioiTinh;

    @Past(message = "Ngày sinh phải ở quá khứ")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;
}
