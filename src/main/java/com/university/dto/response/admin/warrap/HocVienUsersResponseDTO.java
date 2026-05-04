package com.university.dto.response.admin.warrap;

import com.university.dto.response.admin.HocVienAdminResponseDTO;
import com.university.dto.response.admin.UsersAdminResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HocVienUsersResponseDTO {

    private UsersAdminResponseDTO userDetails;

    private HocVienAdminResponseDTO hocVienDetails;
}
