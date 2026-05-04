package com.university.dto.request.admin;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionsAdminRequestDTO {
    @NotNull(message = "Id vai trò không được để trống")
    private UUID roleId;
    @NotNull(message = "Id quyền không được để trống")
    private UUID permissionsId;
}