package com.university.dto.response.admin;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionsAdminResponseDTO {
    private UUID id;
    private String maPermissions;
    private String moTa;
}