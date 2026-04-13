package com.university.dto.request.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.FileEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileStorageAdminRequestDTO {

    @NotBlank(message = "Tên file không được để trống")
    private String fileName;
    private FileEnum fileType;
    private Float fileSize;
    @NotBlank(message = "Đường dẫn không được để trống")
    private String fileUrl;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createdAt;
    @NotNull(message = "UsersID không được để trống")
    private UUID usersID;

}