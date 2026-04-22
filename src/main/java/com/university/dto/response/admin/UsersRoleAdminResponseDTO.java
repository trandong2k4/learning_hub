package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsersRoleAdminResponseDTO {

    private UUID id;
    private UUID userId;
    private UUID roleId;
    private String userName;

    @JsonFormat(pattern = "dd/MM/yyyy : HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy : HH:mm:ss")
    private LocalDateTime updatedAt;

    private String maRole;

    public interface UserRoleView {
        UUID getId();

        String getMaRole();

        String getUserName();
    }
}