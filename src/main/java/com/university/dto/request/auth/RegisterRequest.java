package com.university.dto.request.auth;

import java.time.LocalDateTime;
import com.university.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String userName;

    private String passWord;

    private LocalDateTime createDate;

    public RegisterRequest(String userName, String passWord, LocalDateTime createDate,
            Role role) {
        this.userName = userName;
        this.passWord = passWord;
        this.createDate = createDate;
    }
}
