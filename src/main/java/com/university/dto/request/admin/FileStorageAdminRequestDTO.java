package com.university.dto.request.admin;

import java.util.UUID;

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
    @NotNull(message = "UsersID không được để trống")
    private UUID usersID;

}