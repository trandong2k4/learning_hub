package com.university.service.admin;

import com.university.dto.request.Notification.NotificationRequest;
import com.university.dto.request.admin.ThongBaoAdminRequestDTO;
import com.university.dto.response.admin.ThongBaoAdminResponseDTO;
import com.university.entity.ThongBao;
import com.university.entity.ThongBaoNguoiDung;
import com.university.entity.Users;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.ThongBaoAdminMapper;
import com.university.repository.admin.ThongBaoAdminRepository;
import com.university.repository.admin.ThongBaoNguoiDungAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import com.university.service.Notification.NotificationService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThongBaoAdminService {

    private final ThongBaoAdminRepository thongBaoAdminRepository;
    private final ThongBaoNguoiDungAdminRepository thongBaoNguoiDungAdminRepository;
    private final UsersAdminRepository usersAdminRepository;
    private final NotificationService notificationService;
    private final ThongBaoAdminMapper thongBaoAdminMapper;

    @Transactional
    public ThongBaoAdminResponseDTO create(ThongBaoAdminRequestDTO request) {
        normalizeRequest(request);

        Users sender = usersAdminRepository.findById(request.getUsersId())
                .orElseThrow(() -> new EntityNotFoundException("Người gửi không tồn tại"));

        ThongBao thongBao = thongBaoAdminMapper.toEntity(request);
        thongBao.setUsers(sender);
        ThongBao saved = thongBaoAdminRepository.save(thongBao);

        addReceivers(saved, request.getUserIds());
        return getById(saved.getId());
    }

    @Transactional
    public ThongBaoAdminResponseDTO send(ThongBaoAdminRequestDTO request) {
        normalizeRequest(request);
        if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
            throw new SimpleMessageException("Danh sách người nhận không được rỗng");
        }

        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setTieuDe(request.getTieuDe());
        notificationRequest.setNoiDung(request.getNoiDung());
        notificationRequest.setLoaiThongBao(request.getLoaiThongBao());
        notificationRequest.setFileThongBao(request.getFileThongBao());
        notificationRequest.setUserIds(request.getUserIds());

        notificationService.sendNotification(notificationRequest, request.getUsersId());

        List<ThongBaoAdminResponseDTO> notifications = thongBaoAdminRepository
                .findAllByUsersIdDTO(request.getUsersId());
        return notifications.stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông báo sau khi gửi"));
    }

    public ThongBaoAdminResponseDTO getById(UUID id) {
        ThongBaoAdminResponseDTO response = thongBaoAdminRepository.findDTOById(id);
        if (response == null) {
            throw new EntityNotFoundException("Thông báo không tồn tại");
        }
        normalizeCount(response);
        return response;
    }

    public List<ThongBaoAdminResponseDTO> getAll() {
        return thongBaoAdminRepository.findAllDTO().stream()
                .peek(this::normalizeCount)
                .toList();
    }

    public List<ThongBaoAdminResponseDTO> getAllBySender(UUID usersId) {
        if (!usersAdminRepository.existsById(usersId)) {
            throw new EntityNotFoundException("Người gửi không tồn tại");
        }
        return thongBaoAdminRepository.findAllByUsersIdDTO(usersId).stream()
                .peek(this::normalizeCount)
                .toList();
    }

    @Transactional
    public ThongBaoAdminResponseDTO update(UUID id, ThongBaoAdminRequestDTO request) {
        normalizeRequest(request);

        ThongBao existing = thongBaoAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Thông báo không tồn tại"));

        Users sender = usersAdminRepository.findById(request.getUsersId())
                .orElseThrow(() -> new EntityNotFoundException("Người gửi không tồn tại"));

        thongBaoAdminMapper.updateEntity(existing, request);
        existing.setUsers(sender);

        ThongBao updated = thongBaoAdminRepository.save(existing);
        return getById(updated.getId());
    }

    @Transactional
    public void delete(UUID id) {
        ThongBao thongBao = thongBaoAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Thông báo không tồn tại"));
        thongBaoAdminRepository.delete(thongBao);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        try {
            thongBaoAdminRepository.deleteAllByIdIn(ids);
        } catch (Exception e) {
            throw new SimpleMessageException("Lỗi khi xóa danh sách: " + e.getMessage());
        }
    }

    private void addReceivers(ThongBao thongBao, List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        Set<UUID> uniqueUserIds = new LinkedHashSet<>(userIds);
        List<ThongBaoNguoiDung> receivers = uniqueUserIds.stream()
                .map(userId -> {
                    Users user = usersAdminRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("Người nhận không tồn tại"));

                    ThongBaoNguoiDung receiver = new ThongBaoNguoiDung();
                    receiver.setThongBao(thongBao);
                    receiver.setUsers(user);
                    receiver.setDaNhan(false);
                    return receiver;
                })
                .toList();

        thongBaoNguoiDungAdminRepository.saveAll(receivers);
    }

    private void normalizeRequest(ThongBaoAdminRequestDTO request) {
        if (request == null) {
            throw new SimpleMessageException("Thông tin thông báo không được để trống");
        }

        request.setTieuDe(request.getTieuDe().trim());
        request.setNoiDung(request.getNoiDung().trim());

        if (request.getFileThongBao() != null) {
            request.setFileThongBao(request.getFileThongBao().trim());
            if (request.getFileThongBao().isBlank()) {
                request.setFileThongBao(null);
            }
        }

        if (request.getTieuDe().length() > 50) {
            throw new SimpleMessageException("Tiêu đề tối đa 50 ký tự");
        }
    }

    private void normalizeCount(ThongBaoAdminResponseDTO response) {
        if (response.getSoNguoiNhan() == null) {
            response.setSoNguoiNhan(0L);
        }
        if (response.getSoNguoiDaNhan() == null) {
            response.setSoNguoiDaNhan(0L);
        }
    }
}
