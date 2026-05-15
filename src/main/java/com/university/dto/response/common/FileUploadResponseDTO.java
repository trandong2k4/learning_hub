package com.university.dto.response.common;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseDTO {
    private UUID id;
    private String fileName;
    private String originalFileName;
    private String fileUrl;
    private String storagePath;
    private Long fileSize;
    private String contentType;
}
