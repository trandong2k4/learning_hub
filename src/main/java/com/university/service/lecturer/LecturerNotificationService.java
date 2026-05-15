package com.university.service.lecturer;

import com.university.dto.request.lecturer.NotificationRequestDTO;
import com.university.dto.response.lecturer.NotificationDetailResponseDTO;
import com.university.dto.response.lecturer.NotificationResponseDTO;
import com.university.entity.DangKyTinChi;
import com.university.entity.ThongBao;
import com.university.entity.ThongBaoNguoiDung;
import com.university.entity.Users;
import com.university.enums.LoaiThongBaoEnum;
import com.university.exception.NotFoundException;
import com.university.repository.admin.UsersAdminRepository;
import com.university.repository.lecturer.LecturerDangKyTinChiRepository;
import com.university.repository.lecturer.LecturerNotificationRepository;
import com.university.repository.lecturer.LecturerThongBaoNguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LecturerNotificationService {

    private final UsersAdminRepository userRepository;
    private final LecturerNotificationRepository thongBaoRepository;
    private final LecturerThongBaoNguoiDungRepository thongBaoNguoiDungRepository;
    private final LecturerDangKyTinChiRepository dangKyTinChiRepository;
    private final LecturerValidationService validationService;

    public NotificationResponseDTO sendNotification(UUID userId, NotificationRequestDTO request) {
        validationService.validateLecturerAssignment(userId, request.getLopHocPhanId());
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại."));

        ThongBao thongBao = new ThongBao();
        thongBao.setTieuDe(request.getTieuDe());
        thongBao.setNoiDung(request.getNoiDung());
        thongBao.setFileThongBao(request.getFileThongBao());
        thongBao.setLoaiThongBao(LoaiThongBaoEnum.THONG_BAO_GIANG_VIEN);
        thongBao.setUsers(user);
        thongBaoRepository.save(thongBao);

        sendToClassStudents(thongBao, request.getLopHocPhanId());

        return new NotificationResponseDTO(thongBao.getId(), thongBao.getTieuDe(), thongBao.getNoiDung(),
                thongBao.getFileThongBao(), thongBao.getCreatedAt());
    }

    public List<NotificationDetailResponseDTO> getMyNotifications(UUID userId) {
        validationService.loadActiveLecturerUser(userId);
        List<ThongBao> thongBaos = thongBaoRepository.findByUsers_IdOrderByCreatedAtDesc(userId);
        return thongBaos.stream()
                .map(tb -> {
                    long total = thongBaoNguoiDungRepository.countByThongBaoId(tb.getId());
                    long received = thongBaoNguoiDungRepository.countReceivedByThongBaoId(tb.getId());
                    return new NotificationDetailResponseDTO(
                            tb.getId(),
                            tb.getTieuDe(),
                            tb.getNoiDung(),
                            tb.getFileThongBao(),
                            tb.getCreatedAt(),
                            total,
                            received);
                })
                .collect(Collectors.toList());
    }

    public void deleteNotification(UUID userId, UUID notificationId) {
        validationService.loadActiveLecturerUser(userId);
        ThongBao thongBao = thongBaoRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Thông báo không tồn tại."));
        if (!thongBao.getUsers().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa thông báo này.");
        }
        thongBaoRepository.delete(thongBao);
    }

    public void sendToClassStudents(Users sender, UUID lopHocPhanId, String title, String content) {
        ThongBao thongBao = new ThongBao();
        thongBao.setTieuDe(title);
        thongBao.setNoiDung(content);
        thongBao.setLoaiThongBao(LoaiThongBaoEnum.THONG_BAO_GIANG_VIEN);
        thongBao.setUsers(sender);
        ThongBao saved = thongBaoRepository.save(thongBao);
        sendToClassStudents(saved, lopHocPhanId);
    }

    public void sendToStudent(Users sender, Users student, String title, String content) {
        ThongBao thongBao = new ThongBao();
        thongBao.setTieuDe(title);
        thongBao.setNoiDung(content);
        thongBao.setLoaiThongBao(LoaiThongBaoEnum.THONG_BAO_GIANG_VIEN);
        thongBao.setUsers(sender);
        ThongBao saved = thongBaoRepository.save(thongBao);

        ThongBaoNguoiDung tnd = new ThongBaoNguoiDung();
        tnd.setUsers(student);
        tnd.setThongBao(saved);
        tnd.setDaNhan(false);
        thongBaoNguoiDungRepository.save(tnd);
    }

    private void sendToClassStudents(ThongBao thongBao, UUID lopHocPhanId) {
        List<DangKyTinChi> registrations = dangKyTinChiRepository.findByLopHocPhan_Id(lopHocPhanId);
        List<ThongBaoNguoiDung> receivers = registrations.stream()
                .map(reg -> {
                    ThongBaoNguoiDung tnd = new ThongBaoNguoiDung();
                    tnd.setUsers(reg.getHocVien().getUsers());
                    tnd.setThongBao(thongBao);
                    tnd.setDaNhan(false);
                    return tnd;
                })
                .collect(Collectors.toList());
        thongBaoNguoiDungRepository.saveAll(receivers);
    }
}
