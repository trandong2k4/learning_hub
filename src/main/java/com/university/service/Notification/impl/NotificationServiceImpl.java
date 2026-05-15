package com.university.service.Notification.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.university.dto.request.Notification.NotificationRequest;
import com.university.dto.response.Notification.NotificationResponse;
import com.university.entity.ThongBao;
import com.university.entity.ThongBaoNguoiDung;
import com.university.entity.Users;
import com.university.exception.NotFoundException;
import com.university.mapper.Notification.NotificationMapper;
import com.university.repository.admin.ThongBaoAdminRepository;
import com.university.repository.admin.ThongBaoNguoiDungAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import com.university.service.Notification.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

        private final ThongBaoAdminRepository thongBaoRepository;
        private final ThongBaoNguoiDungAdminRepository tbndRepository;
        private final NotificationMapper mapper;
        private final UsersAdminRepository usersRepository;

        @Override
        public void sendNotification(NotificationRequest request, UUID senderId) {

                Users sender = usersRepository.findById(senderId)
                                .orElseThrow(() -> new NotFoundException("Không tìm thấy người gửi"));

                ThongBao tb = new ThongBao();
                tb.setTieuDe(request.getTieuDe());
                tb.setNoiDung(request.getNoiDung());
                tb.setLoaiThongBao(request.getLoaiThongBao());
                tb.setUsers(sender);

                thongBaoRepository.save(tb);

                List<ThongBaoNguoiDung> list = request.getUserIds().stream()
                                .map(userId -> {
                                        Users user = usersRepository.findById(userId)
                                                        .orElseThrow(() -> new NotFoundException(
                                                                        "Không tìm thấy người nhận"));

                                        ThongBaoNguoiDung tbnd = new ThongBaoNguoiDung();
                                        tbnd.setUsers(user);
                                        tbnd.setThongBao(tb);
                                        tbnd.setDaNhan(false);

                                        return tbnd;
                                }).toList();

                tbndRepository.saveAll(list);
        }

        @Override
        public List<NotificationResponse> getMyNotifications(UUID userId) {

                return tbndRepository.findByUsers_Id(userId)
                                .stream()
                                .map(tbnd -> mapper.toResponse(
                                                tbnd.getThongBao(),
                                                tbnd.getDaNhan()))
                                .toList();
        }

        @Override
        public List<NotificationResponse> getUnreadNotifications(UUID userId) {

                return tbndRepository.findByUsers_IdAndDaNhanFalse(userId)
                                .stream()
                                .map(tbnd -> mapper.toResponse(
                                                tbnd.getThongBao(),
                                                false))
                                .toList();
        }

        @Override
        public void markAsRead(UUID thongBaoId, UUID userId) {

                ThongBaoNguoiDung tbnd = tbndRepository
                                .findByThongBao_IdAndUsers_Id(thongBaoId, userId)
                                .orElseThrow(() -> new NotFoundException("Thông báo không tồn tại"));

                tbnd.setDaNhan(true);
        }
}
