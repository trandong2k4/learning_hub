package com.university.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDTO {
    private String refreshToken;
}
