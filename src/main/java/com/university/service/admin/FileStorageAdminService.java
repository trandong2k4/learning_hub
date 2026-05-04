package com.university.service.admin;

import com.university.dto.request.admin.FileStorageAdminRequestDTO;
import com.university.dto.response.admin.FileStorageAdminResponseDTO;
import com.university.entity.FileStorage;
import com.university.entity.Users;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.FileStorageAdminMapper;
import com.university.repository.admin.FileStorageAdminRepository;
import com.university.repository.admin.UsersAdminRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageAdminService {

    private final FileStorageAdminRepository fileStorageRepository;
    private final UsersAdminRepository usersRepository;
    private final FileStorageAdminMapper fileStorageMapper;

    @Transactional
    public FileStorageAdminResponseDTO createFileStorage(FileStorageAdminRequestDTO request) {
        Users user = usersRepository.findById(request.getUsersID())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        FileStorage fileStorage = fileStorageMapper.toEntity(request);
        fileStorage.setUsers(user);
        fileStorage.setCreatedAt(LocalDateTime.now());

        FileStorage saved = fileStorageRepository.save(fileStorage);
        return fileStorageMapper.toResponseDTO(saved);
    }

    @Transactional
    public FileStorageAdminResponseDTO updateFileStorage(UUID id, FileStorageAdminRequestDTO request) {
        FileStorage existing = fileStorageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File không tồn tại"));

        Users user = usersRepository.findById(request.getUsersID())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        fileStorageMapper.updateEntity(existing, request);
        existing.setUsers(user);

        FileStorage updated = fileStorageRepository.save(existing);
        return fileStorageMapper.toResponseDTO(updated);
    }

    public FileStorageAdminResponseDTO getFileStorageById(UUID id) {
        FileStorage fileStorage = fileStorageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File không tồn tại"));
        return fileStorageMapper.toResponseDTO(fileStorage);
    }

    public List<FileStorageAdminResponseDTO.FileStorageView> getAllFileStorage() {
        return fileStorageRepository.findAllProjectedBy();
    }

    @Transactional
    public void deleteFileStorage(UUID id) {
        FileStorage file = fileStorageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File không tồn tại"));

        fileStorageRepository.delete(file);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            // Kiem tra user dang co trong cac db khac khong
            // for (UUID uuid : ids) {
            // if (usersAdminRepository.) {

            // }
            // }
            fileStorageRepository.deleteAllByIdIn(ids);

        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}
