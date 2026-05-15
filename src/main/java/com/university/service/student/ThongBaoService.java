package com.university.service.student;

import com.university.config.SecurityUtils;
import com.university.dto.request.student.ThongBaoRequest;
import com.university.dto.response.student.ThongBaoResponse;
import com.university.entity.ThongBaoNguoiDung;
import com.university.repository.student.ThongBaoNguoiDungRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThongBaoService {

    private final ThongBaoNguoiDungRepository thongBaoNguoiDungRepository;

    @Transactional(readOnly = true)
    public List<ThongBaoResponse> getDanhSachThongBao() {
        return thongBaoNguoiDungRepository.findThongBaoByUsersId(SecurityUtils.getCurrentUserId());
    }

    @Transactional
    public void danhDauDaDoc(ThongBaoRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        ThongBaoNguoiDung thongBaoNguoiDung = thongBaoNguoiDungRepository
                .findByIdAndUsersId(request.getThongBaoNguoiDungId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay thong bao nguoi dung"));

        thongBaoNguoiDung.setDaNhan(true);
    }

    @Transactional
    public void danhDauTatCaDaDoc() {
        thongBaoNguoiDungRepository.markAllAsReadByUsersId(SecurityUtils.getCurrentUserId());
    }

}
