package com.university.service.student;

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
@Transactional
public class ThongBaoService {
    private final ThongBaoNguoiDungRepository thongBaoNguoiDungRepository;
    public List<ThongBaoResponse> getThongBaoByUsersId(UUID usersId) {
        return thongBaoNguoiDungRepository.findThongBaoByUsersId(usersId);
    }
    public void danhDauDaDoc(ThongBaoRequest request) {
        ThongBaoNguoiDung thongBaoNguoiDung = thongBaoNguoiDungRepository.findByUsersIdAndThongBaoId(request.getUsersId(), request.getThongbaonguoidungId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo người dùng"));
        thongBaoNguoiDung.setDaNhan(true);
        thongBaoNguoiDungRepository.save(thongBaoNguoiDung);
    }
    public void danhDauTatCaDaDoc(ThongBaoRequest request) {
        List<ThongBaoNguoiDung> thongBaoNguoiDungList = thongBaoNguoiDungRepository.findByUsersIdAndDaNhanFalse(request.getUsersId());
        thongBaoNguoiDungList.forEach(thongBaoNguoiDung -> thongBaoNguoiDung.setDaNhan(true));
        thongBaoNguoiDungRepository.saveAll(thongBaoNguoiDungList);

    }
    public List<ThongBaoResponse> getDanhSachThongBao(UUID usersId) {
        return thongBaoNguoiDungRepository.findThongBaoByUsersId(usersId);
    }
}
