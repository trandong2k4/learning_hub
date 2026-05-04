package com.university.dto.response.admin.warrap;

import com.university.dto.response.admin.NhanVienAdminResponseDTO;
import com.university.dto.response.admin.UsersAdminResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienUsersResponseDTO {

    private UsersAdminResponseDTO userDetails;

    private NhanVienAdminResponseDTO nhanVienDetails;
}
