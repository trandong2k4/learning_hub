package com.university.service.admin;

import com.university.dto.request.admin.ThongBaoNguoiDungAdminRequestDTO;
import com.university.dto.response.admin.ThongBaoNguoiDungAdminResponseDTO;
import com.university.entity.ThongBao;
import com.university.entity.ThongBaoNguoiDung;
import com.university.entity.Users;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.ThongBaoNguoiDungAdminMapper;
import com.university.repository.admin.ThongBaoAdminRepository;
import com.university.repository.admin.ThongBaoNguoiDungAdminRepository;
import com.university.repository.admin.UsersAdminRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThongBaoNguoiDungAdminService {

    private final ThongBaoNguoiDungAdminRepository thongBaoNguoiDungAdminRepository;
    private final ThongBaoAdminRepository thongBaoAdminRepository;
    private final UsersAdminRepository usersAdminRepository;
    private final ThongBaoNguoiDungAdminMapper thongBaoNguoiDungAdminMapper;

    @Transactional
    public ThongBaoNguoiDungAdminResponseDTO create(ThongBaoNguoiDungAdminRequestDTO request) {
        ThongBao thongBao = thongBaoAdminRepository.findById(request.getThongBaoId())
                .orElseThrow(() -> new EntityNotFoundException("Thông báo không tồn tại"));
        Users user = usersAdminRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Người dùng không tồn tại"));

        if (thongBaoNguoiDungAdminRepository.existsByThongBao_IdAndUsers_Id(thongBao.getId(), user.getId())) {
            throw new SimpleMessageException("Người dùng đã có thông báo này");
        }

        ThongBaoNguoiDung entity = thongBaoNguoiDungAdminMapper.toEntity(request);
        entity.setThongBao(thongBao);
        entity.setUsers(user);

        return thongBaoNguoiDungAdminMapper.toResponseDTO(thongBaoNguoiDungAdminRepository.save(entity));
    }

    public ThongBaoNguoiDungAdminResponseDTO getById(UUID id) {
        ThongBaoNguoiDung entity = thongBaoNguoiDungAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Thông báo người dùng không tồn tại"));
        return thongBaoNguoiDungAdminMapper.toResponseDTO(entity);
    }

    public List<ThongBaoNguoiDungAdminResponseDTO> getAll() {
        return thongBaoNguoiDungAdminRepository.findAllDTO();
    }

    public List<ThongBaoNguoiDungAdminResponseDTO> getAllByThongBao(UUID thongBaoId) {
        if (!thongBaoAdminRepository.existsById(thongBaoId)) {
            throw new EntityNotFoundException("Thông báo không tồn tại");
        }
        return thongBaoNguoiDungAdminRepository.findAllByThongBaoIdDTO(thongBaoId);
    }

    public List<ThongBaoNguoiDungAdminResponseDTO> getAllByUser(UUID userId) {
        if (!usersAdminRepository.existsById(userId)) {
            throw new EntityNotFoundException("Người dùng không tồn tại");
        }
        return thongBaoNguoiDungAdminRepository.findAllByUserIdDTO(userId);
    }

    @Transactional
    public ThongBaoNguoiDungAdminResponseDTO update(UUID id, ThongBaoNguoiDungAdminRequestDTO request) {
        ThongBaoNguoiDung existing = thongBaoNguoiDungAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Thông báo người dùng không tồn tại"));

        ThongBao thongBao = thongBaoAdminRepository.findById(request.getThongBaoId())
                .orElseThrow(() -> new EntityNotFoundException("Thông báo không tồn tại"));
        Users user = usersAdminRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Người dùng không tồn tại"));

        if (thongBaoNguoiDungAdminRepository.existsByThongBao_IdAndUsers_IdAndIdNot(
                thongBao.getId(), user.getId(), id)) {
            throw new SimpleMessageException("Người dùng đã có thông báo này");
        }

        thongBaoNguoiDungAdminMapper.updateEntity(existing, request);
        existing.setThongBao(thongBao);
        existing.setUsers(user);

        return thongBaoNguoiDungAdminMapper.toResponseDTO(thongBaoNguoiDungAdminRepository.save(existing));
    }

    @Transactional
    public void delete(UUID id) {
        ThongBaoNguoiDung entity = thongBaoNguoiDungAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Thông báo người dùng không tồn tại"));
        thongBaoNguoiDungAdminRepository.delete(entity);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            thongBaoNguoiDungAdminRepository.deleteAllByIdIn(ids);
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }
}
