package com.university.dto.request.admin.warrap;

import com.university.dto.request.admin.NhanVienAdminRequestDTO;
import com.university.dto.request.admin.UsersAdminRequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienCreateRequestDTO {

    @Valid
    @NotNull(message = "Thông tin tài khoản không được để trống")
    private UsersAdminRequestDTO userDetails;

    @Valid
    @NotNull(message = "Thông tin nhân viên không được để trống")
    private NhanVienAdminRequestDTO nhanVienDetails;
}
