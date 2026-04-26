package com.university.mapper.admin;

import com.university.dto.request.admin.FileStorageAdminRequestDTO;
import com.university.dto.response.admin.FileStorageAdminResponseDTO;
import com.university.entity.FileStorage;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FileStorageAdminMapper {

    public FileStorage toEntity(FileStorageAdminRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        FileStorage fileStorage = new FileStorage();
        fileStorage.setFileName(dto.getFileName());
        fileStorage.setFileType(dto.getFileType());
        fileStorage.setFileSize(dto.getFileSize());
        fileStorage.setFileUrl(dto.getFileUrl());
        fileStorage.setCreatedAt(LocalDateTime.now());
        return fileStorage;
    }

    public void updateEntity(FileStorage fileStorage, FileStorageAdminRequestDTO dto) {
        if (dto == null || fileStorage == null) {
            return;
        }
        fileStorage.setFileName(dto.getFileName());
        fileStorage.setFileType(dto.getFileType());
        fileStorage.setFileSize(dto.getFileSize());
        fileStorage.setFileUrl(dto.getFileUrl());
    }

    public FileStorageAdminResponseDTO toResponseDTO(FileStorage entity) {
        if (entity == null) {
            return null;
        }

        FileStorageAdminResponseDTO dto = new FileStorageAdminResponseDTO();
        dto.setId(entity.getId());
        dto.setFileName(entity.getFileName());
        dto.setFileType(entity.getFileType());
        dto.setFileSize(entity.getFileSize());
        dto.setFileUrl(entity.getFileUrl());
        dto.setCreatedAt(entity.getCreatedAt());

        // Ánh xạ ID của user từ Entity sang DTO
        if (entity.getUsers() != null) {
            dto.setUsersID(entity.getUsers().getId());
        }

        return dto;
    }
}