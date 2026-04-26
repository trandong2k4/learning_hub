package com.university.dto.response.admin;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionsAdminResponseDTO {

    private UUID id;
    private UUID roleId;
    private UUID permissionsId;
    private Boolean status;

    public interface RolePermissionsView {
        UUID getId();

        String getRoleId();

        String getPermissionsId();

        Boolean getStatus();
    }

}