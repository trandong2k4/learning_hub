package com.university.service.student;

import com.university.config.SecurityUtils;
import com.university.dto.request.student.ThongBaoRequest;
import com.university.dto.response.student.ThongBaoResponse;
import com.university.entity.HocVien;
import com.university.entity.ThongBaoNguoiDung;
import com.university.repository.student.HocVienProfileRepository;
import com.university.repository.student.ThongBaoNguoiDungRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ThongBaoService {

    private final ThongBaoNguoiDungRepository thongBaoNguoiDungRepository;
    private final HocVienProfileRepository hocVienProfileRepository;

    public List<ThongBaoResponse> getDanhSachThongBao() {
        return thongBaoNguoiDungRepository.findThongBaoByUsersId(getCurrentUserId());
    }

    public void danhDauDaDoc(ThongBaoRequest request) {
        UUID userId = getCurrentUserId();
        ThongBaoNguoiDung thongBaoNguoiDung = thongBaoNguoiDungRepository
                .findByIdAndUsersId(request.getThongBaoNguoiDungId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay thong bao nguoi dung"));

        thongBaoNguoiDung.setDaNhan(true);
        thongBaoNguoiDungRepository.save(thongBaoNguoiDung);
    }

    public void danhDauTatCaDaDoc() {
        UUID userId = getCurrentUserId();
        List<ThongBaoNguoiDung> thongBaoNguoiDungList =
                thongBaoNguoiDungRepository.findByUsersIdAndDaNhanFalse(userId);

        thongBaoNguoiDungList.forEach(thongBaoNguoiDung -> thongBaoNguoiDung.setDaNhan(true));
        thongBaoNguoiDungRepository.saveAll(thongBaoNguoiDungList);
    }

    private UUID getCurrentUserId() {
        UUID hocVienId = SecurityUtils.getCurrentHocVienId();
        HocVien hocVien = hocVienProfileRepository.findById(hocVienId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay hoc vien"));
        return hocVien.getUsers().getId();
    }
}
