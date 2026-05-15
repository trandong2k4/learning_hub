package com.university.dto.request.admin.warrap;

import com.university.dto.request.admin.HocVienCreateDetailsDTO;
import com.university.dto.request.admin.UsersAdminRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HocVienFullCreateRequestDTO {

    @Valid
    @NotNull(message = "Thông tin tài khoản không được để trống")
    private UsersAdminRequestDTO userDetails;

    @Valid
    @NotNull(message = "Thông tin học viên không được để trống")
    private HocVienCreateDetailsDTO hocVienDetails;
}
