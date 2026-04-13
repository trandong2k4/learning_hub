package com.university.dto.request.admin;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionsAdminRequestDTO {
    @NotBlank(message = "Id vai trò không được để trống")
    private UUID roleId;
    @NotBlank(message = "Id quyền không được để trống")
    private UUID permissionsId;
}