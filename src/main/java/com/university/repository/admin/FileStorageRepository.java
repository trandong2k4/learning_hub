package com.university.repository.admin;

import com.university.dto.response.admin.FileStorageAdminResponseDTO.FileStorageView;
import com.university.entity.FileStorage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileStorageRepository extends JpaRepository<FileStorage, UUID> {

    List<FileStorageView> findAllProjectedBy();
}
