package com.university.repository.lecturer;

import com.university.entity.FileStorage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerFileStorageRepository extends JpaRepository<FileStorage, UUID> {
    List<FileStorage> findByUsers_IdAndFileType(UUID usersId, com.university.enums.FileEnum fileType);
}
