package com.university.dto.response.auth;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDTO {
    private UUID id;
    private String username;
    private LocalDateTime createDate;

    public RegisterResponseDTO(UUID id, String username, String fullName, LocalDateTime createAt) {
        this.id = id;
        this.username = username;
        this.createDate = createAt;
    }
}
