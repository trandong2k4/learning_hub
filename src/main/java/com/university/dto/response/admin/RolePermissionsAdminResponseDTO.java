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

    public interface RolePermissionsView {
        UUID getId();
    }

}