package com.university.service.admin;

import com.university.dto.request.admin.NhanVienAdminRequestDTO;
import com.university.dto.request.admin.warrap.NhanVienCreateRequestDTO;
import com.university.dto.response.admin.NhanVienAdminResponseDTO;
import com.university.dto.response.admin.warrap.NhanVienUsersResponseDTO;
import com.university.entity.NhanVien;
import com.university.entity.Users;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.NhanVienAdminMapper;
import com.university.mapper.admin.UsersAdminMapper;
import com.university.repository.admin.NhanVienAdminRepository;
import com.university.repository.admin.UsersAdminRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NhanVienAdminService {

    private final NhanVienAdminRepository nhanVienAdminRepository;
    private final UsersAdminRepository usersRepository;
    private final NhanVienAdminMapper nhanVienAdminMapper;
    private final UsersAdminMapper usersAdminMapper;

    @Transactional
    public NhanVienUsersResponseDTO createDTO(NhanVienCreateRequestDTO nhanVienCreateRequestDTO) {
        Users users = usersAdminMapper.toEntity(nhanVienCreateRequestDTO.getUserDetails());
        usersRepository.save(users);
        NhanVien nhanVien = nhanVienAdminMapper.toEntity(nhanVienCreateRequestDTO.getNhanVienDetails());
        nhanVien.setUsers(users);

        nhanVienAdminRepository.save(nhanVien);

        NhanVienUsersResponseDTO nhanVienUsersRequestDTO = new NhanVienUsersResponseDTO();
        nhanVienUsersRequestDTO.setUserDetails(usersAdminMapper.toResponseDTO(users));
        nhanVienUsersRequestDTO.setNhanVienDetails(nhanVienAdminMapper.toResponseDTO(nhanVien));
        return nhanVienUsersRequestDTO;
    }

    public NhanVienAdminResponseDTO getNhanVienById(UUID id) {
        NhanVien nhanVien = nhanVienAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nhân viên không tồn tại"));
        return nhanVienAdminMapper.toResponseDTO(nhanVien);
    }

    public List<NhanVienAdminResponseDTO> getAll() {
        return nhanVienAdminRepository.findAllDTO();
    }

    @Transactional
    public NhanVienAdminResponseDTO update(UUID id, NhanVienAdminRequestDTO request) {
        NhanVien existing = nhanVienAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nhân viên không tồn tại"));
        nhanVienAdminMapper.updateEntity(existing, request);
        NhanVien updated = nhanVienAdminRepository.save(existing);
        return nhanVienAdminMapper.toResponseDTO(updated);
    }

    @Transactional
    public void deleteNhanVien(UUID id) {
        NhanVien nv = nhanVienAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nhân viên không tồn tại"));

        nhanVienAdminRepository.delete(nv);
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
            nhanVienAdminRepository.deleteAllByIdIn(ids);

        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}
