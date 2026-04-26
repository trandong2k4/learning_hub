package com.university.repository.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.university.dto.response.admin.FileStorageAdminResponseDTO;
import com.university.entity.FileStorage;

public interface FileStorageAdminRepository extends JpaRepository<FileStorage, UUID> {
    List<FileStorageAdminResponseDTO.FileStorageView> findAllProjectedBy();
}