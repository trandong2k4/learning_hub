package com.university.dto.response.auth;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDTO {
    private UUID id;
    private String userName;
    private String fullName;
    private String accessToken;
    private String refreshToken;
    List<String> dRole;
    private String message;

}
