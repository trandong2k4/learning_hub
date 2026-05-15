package com.university.repository.common;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.university.entity.FileStorage;

@Repository
public interface FileStorageRepository extends JpaRepository<FileStorage, UUID> {
    Optional<FileStorage> findByFileName(String fileName);
}
