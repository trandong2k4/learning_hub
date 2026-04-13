package com.university.dto.response.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.university.enums.FileEnum;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileStorageAdminResponseDTO {

    private UUID id;
    private String fileName;
    private FileEnum fileType;
    private Float fileSize;
    private String fileUrl;
    @JsonFormat(pattern = "dd/MM/yyyy : hh:mm:ss")
    private LocalDateTime createdAt;
    private UUID usersID;

    public interface FileStorageView {
        UUID getId();
    }

}