package com.university.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionsAdminRequestDTO {
    @NotBlank(message = "Mã quyền không được để trống")
    private String maPermissions;
    private String moTa;
}