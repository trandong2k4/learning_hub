package com.university.service.admin;

import com.university.dto.response.admin.AdminDashboardResponseDTO;
import com.university.dto.response.admin.AdminDashboardResponseDTO.*;
import com.university.dto.response.admin.HocPhiAdminResponseDTO;
import com.university.dto.response.admin.PhanHoiLienHeAdminResponseDTO;
import com.university.dto.response.admin.ThongBaoAdminResponseDTO;
import com.university.enums.TrangThaiXuLyLienHeEnum;
import com.university.repository.admin.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

        private final UsersAdminRepository usersRepository;
        private final HocVienAdminRepository hocVienRepository;
        private final NhanVienAdminRepository nhanVienRepository;
        private final LopHocPhanAdminRepository lopHocPhanRepository;
        private final HocPhiAdminRepository hocPhiRepository;
        private final PhanHoiLienHeAdminRepository lienHeRepository;
        private final ThongBaoAdminRepository thongBaoRepository;

        public AdminDashboardResponseDTO getDashboard() {
                log.debug("Bat dau tai dashboard admin");

                CompletableFuture<Long> usersFuture = CompletableFuture.supplyAsync(usersRepository::count);
                CompletableFuture<Long> hocVienFuture = CompletableFuture.supplyAsync(hocVienRepository::count);
                CompletableFuture<Long> giangVienFuture = CompletableFuture
                                .supplyAsync(nhanVienRepository::countGiangVien);
                CompletableFuture<Long> lopHpFuture = CompletableFuture.supplyAsync(lopHocPhanRepository::count);
                CompletableFuture<Double> hocPhiTongFuture = CompletableFuture
                                .supplyAsync(hocPhiRepository::getTongSoTien);
                CompletableFuture<List<HocPhiAdminResponseDTO.DashboardTheoThang>> revenueFuture = CompletableFuture
                                .supplyAsync(hocPhiRepository::getDashboardTheoThang);
                CompletableFuture<List<Object[]>> hocVienTheoNamFuture = CompletableFuture
                                .supplyAsync(hocVienRepository::countByNamNhapHocRaw);
                CompletableFuture<List<Object[]>> hocVienTheoNganhFuture = CompletableFuture
                                .supplyAsync(hocVienRepository::countByNganhRaw);
                CompletableFuture<List<PhanHoiLienHeAdminResponseDTO>> recentLienHeFuture = CompletableFuture
                                .supplyAsync(() -> {
                                        List<PhanHoiLienHeAdminResponseDTO> all = lienHeRepository.findAllDTO();
                                        return all.stream().limit(5).collect(Collectors.toList());
                                });
                CompletableFuture<List<ThongBaoAdminResponseDTO>> recentThongBaoFuture = CompletableFuture
                                .supplyAsync(() -> {
                                        List<ThongBaoAdminResponseDTO> all = thongBaoRepository.findAllDTO();
                                        return all.stream().limit(5).collect(Collectors.toList());
                                });

                CompletableFuture.allOf(
                                usersFuture, hocVienFuture, giangVienFuture, lopHpFuture,
                                hocPhiTongFuture, revenueFuture,
                                hocVienTheoNamFuture, hocVienTheoNganhFuture,
                                recentLienHeFuture, recentThongBaoFuture).join();

                // Liên hệ theo trạng thái
                long lienHeChua = lienHeRepository.countByTrangThai(TrangThaiXuLyLienHeEnum.CHUA_XU_LY);
                long lienHeDang = lienHeRepository.countByTrangThai(TrangThaiXuLyLienHeEnum.DANG_XU_LY);
                long lienHeDa = lienHeRepository.countByTrangThai(TrangThaiXuLyLienHeEnum.DA_XU_LY);

                // Doanh thu theo tháng
                List<DoanhThuThang> doanhThuList = new ArrayList<>();
                for (HocPhiAdminResponseDTO.DashboardTheoThang item : revenueFuture.join()) {
                        doanhThuList.add(DoanhThuThang.builder()
                                        .thang(item.getThang())
                                        .nam(item.getNam())
                                        .soLuong(item.getSoLuong())
                                        .tongTien(item.getTongTien())
                                        .tienDaThu(item.getTienDaThu())
                                        .build());
                }

                // Học viên theo năm
                List<HocVienTheoNam> hocVienTheoNamList = hocVienTheoNamFuture.join().stream()
                                .map(row -> HocVienTheoNam.builder()
                                                .nam(((Number) row[0]).intValue())
                                                .soHocVien(((Number) row[1]).longValue())
                                                .build())
                                .sorted(Comparator.comparing(HocVienTheoNam::getNam))
                                .collect(Collectors.toList());

                // Học viên theo ngành
                List<HocVienTheoNganh> hocVienTheoNganhList = hocVienTheoNganhFuture.join().stream()
                                .map(row -> HocVienTheoNganh.builder()
                                                .tenNganh((String) row[0])
                                                .soHocVien(((Number) row[1]).longValue())
                                                .build())
                                .collect(Collectors.toList());

                // Hoạt động gần đây
                List<HoatDongGanDay> hoatDongList = new ArrayList<>();

                for (PhanHoiLienHeAdminResponseDTO lh : recentLienHeFuture.join()) {
                        hoatDongList.add(HoatDongGanDay.builder()
                                        .id("lh-" + lh.getId())
                                        .loai("LIEN_HE")
                                        .tieuDe("Liên hệ: " + lh.getHoTen())
                                        .moTa(lh.getChuDe() + " - " + lh.getTrangThai())
                                        .thoiGian(lh.getNgayTao() != null ? lh.getNgayTao() : LocalDateTime.now())
                                        .build());
                }

                for (ThongBaoAdminResponseDTO tb : recentThongBaoFuture.join()) {
                        hoatDongList.add(HoatDongGanDay.builder()
                                        .id("tb-" + tb.getId())
                                        .loai("THONG_BAO")
                                        .tieuDe(tb.getTieuDe())
                                        .moTa(tb.getNoiDung() != null && tb.getNoiDung().length() > 80
                                                        ? tb.getNoiDung().substring(0, 80) + "..."
                                                        : tb.getNoiDung())
                                        .thoiGian(tb.getCreatedAt() != null ? tb.getCreatedAt() : LocalDateTime.now())
                                        .build());
                }

                hoatDongList.sort((a, b) -> b.getThoiGian().compareTo(a.getThoiGian()));
                if (hoatDongList.size() > 10) {
                        hoatDongList = hoatDongList.subList(0, 10);
                }

                Double tongHocPhiRaw = hocPhiTongFuture.join();
                double tongHocPhi = tongHocPhiRaw != null ? tongHocPhiRaw : 0.0;

                return AdminDashboardResponseDTO.builder()
                                .tongNguoiDung(usersFuture.join())
                                .tongHocVien(hocVienFuture.join())
                                .tongGiangVien(giangVienFuture.join())
                                .tongLopHocPhan(lopHpFuture.join())
                                .tongHocPhi(tongHocPhi)
                                .hocPhiDaThu(tongHocPhi * 0.7)
                                .hocPhiConNo(tongHocPhi * 0.3)
                                .hocVienMoi(0)
                                .lopDangHoatDong(lopHpFuture.join())
                                .lienHeChuaXuLy(lienHeChua)
                                .lienHeDangXuLy(lienHeDang)
                                .lienHeDaXuLy(lienHeDa)
                                .doanhThuTheoThang(doanhThuList)
                                .hocVienTheoNam(hocVienTheoNamList)
                                .hocVienTheoNganh(hocVienTheoNganhList)
                                .hoatDongGanDay(hoatDongList)
                                .build();
        }
}
