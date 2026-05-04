package com.university.service.admin;

import com.university.dto.request.admin.BaiVietAdminRequestDTO;
import com.university.dto.response.admin.BaiVietAdminResponseDTO;
import com.university.entity.BaiViet;
import com.university.entity.Users;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.BaiVietAdminMapper;
import com.university.repository.admin.BaiVietAdminRepository;
import com.university.repository.admin.ThongBaoNguoiDungAdminRepository;
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
public class ThongBaoNguoiDungAdminService {

    private final ThongBaoNguoiDungAdminRepository thongBaoNguoiDungAdminRepository;
    private final BaiVietAdminRepository baiVietAdminRepository;
    private final UsersAdminRepository usersRepository;
    private final BaiVietAdminMapper baiVietMapper;

    @Transactional
    public BaiVietAdminResponseDTO createBaiViet(BaiVietAdminRequestDTO request) {
        Users user = usersRepository.findById(request.getUsersId())
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));

        BaiViet baiViet = baiVietMapper.toEntity(request);
        baiViet.setUsers(user);
        baiViet.setCreatedAt(LocalDateTime.now());
        baiViet.setUpdatedAt(LocalDateTime.now());

        BaiViet saved = baiVietAdminRepository.save(baiViet);
        return baiVietMapper.toResponseDTO(saved);
    }

    @Transactional
    public BaiVietAdminResponseDTO updateBaiViet(UUID id, BaiVietAdminRequestDTO request) {
        BaiViet existing = baiVietAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bài viết không tồn tại"));

        Users user = usersRepository.findById(request.getUsersId())
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));

        baiVietMapper.updateEntity(existing, request);
        existing.setUsers(user);
        existing.setUpdatedAt(LocalDateTime.now());

        BaiViet updated = baiVietAdminRepository.save(existing);
        return baiVietMapper.toResponseDTO(updated);
    }

    public BaiVietAdminResponseDTO getBaiVietById(UUID id) {
        BaiViet baiViet = baiVietAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bài viết không tồn tại"));
        return baiVietMapper.toResponseDTO(baiViet);
    }

    public List<BaiVietAdminResponseDTO.BaiVietView> getALlBaiViet() {
        return baiVietAdminRepository.findAllBaiVietView();
    }

    @Transactional
    public void deleteBaiViet(UUID id) {
        BaiViet bv = baiVietAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bài viết không tồn tại"));

        baiVietAdminRepository.delete(bv);
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
            thongBaoNguoiDungAdminRepository.deleteAllByIdIn(ids);

        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}
