package com.university.dto.request.admin;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleAdminRequestDTO {
    @NotNull(message = "Id users không được để trống")
    private UUID usersId;
    @NotNull(message = "Id role không được để trống")
    private UUID roleId;
}