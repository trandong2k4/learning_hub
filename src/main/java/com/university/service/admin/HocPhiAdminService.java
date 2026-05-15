package com.university.service.admin;

import com.university.dto.response.admin.HocPhiAdminResponseDTO;
import com.university.entity.HocPhi;
import com.university.exception.SimpleMessageException;
import com.university.mapper.admin.HocPhiAdminMapper;
import com.university.repository.admin.DangKyTinChiAdminRepository;
import com.university.repository.admin.HocPhiAdminRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HocPhiAdminService {

    private static final double GIA_TIEN_MOT_TIN_CHI = 700_000.0;

    private final HocPhiAdminRepository hocPhiAdminRepository;
    private final DangKyTinChiAdminRepository dangKyTinChiRepository;
    private final HocPhiAdminMapper hocPhiMapper;

    public HocPhiAdminResponseDTO getById(UUID id) {
        HocPhi entity = hocPhiAdminRepository.findById(id)
                .orElseThrow(() -> new SimpleMessageException("Học phí không tồn tại"));
        return hocPhiMapper.toResponseDTO(entity);
    }

    public List<HocPhiAdminResponseDTO> getAllHocPhi() {
        return hocPhiAdminRepository.findAll().stream()
                .map(hocPhiMapper::toResponseDTO)
                .toList();
    }

    public List<HocPhiAdminResponseDTO.HocPhiView> getAllHocPhiView() {
        return hocPhiAdminRepository.findAllProjectedBy();
    }

    public List<HocPhiAdminResponseDTO> getHocPhiByHocKi(UUID hocKiId) {
        return hocPhiAdminRepository.findAllByHocKiId(hocKiId).stream()
                .map(hocPhiMapper::toResponseDTO)
                .toList();
    }

    public List<HocPhiAdminResponseDTO> getHocPhiByHocVien(UUID hocVienId) {
        return hocPhiAdminRepository.findAllByHocVienId(hocVienId).stream()
                .map(hocPhiMapper::toResponseDTO)
                .toList();
    }

    public HocPhiAdminResponseDTO.DashboardTongQuan getDashboardTongQuan() {
        return hocPhiAdminRepository.getDashboardTongQuan();
    }

    public List<HocPhiAdminResponseDTO.DashboardTheoHocKi> getDashboardTheoHocKi() {
        return hocPhiAdminRepository.getDashboardTheoHocKi();
    }

    public List<HocPhiAdminResponseDTO.DashboardTheoThang> getDashboardTheoThang() {
        return hocPhiAdminRepository.getDashboardTheoThang();
    }

    public List<HocPhiAdminResponseDTO.DashboardTopNo> getDashboardTopNo() {
        return hocPhiAdminRepository.getDashboardTopNo();
    }

    public Long getTongTinChiByHocVien(UUID hocVienId) {
        Long tongTinChi = hocPhiAdminRepository.getTongTinChiByHocVien(hocVienId);
        if (tongTinChi == null) {
            throw new SimpleMessageException("Học viên chưa đăng ký tín chỉ nào");
        }
        return tongTinChi;
    }

    public Long getTongTinChiByHocVienAndHocKi(UUID hocVienId, UUID hocKiId) {
        Long tongTinChi = hocPhiAdminRepository.getTongTinChiByHocVienAndHocKi(hocVienId, hocKiId);
        if (tongTinChi == null) {
            throw new SimpleMessageException("Học viên chưa đăng ký tín chỉ trong học kì này");
        }
        return tongTinChi;
    }

    public List<HocPhiAdminResponseDTO.DangKyTinChiItem> getDangKyTinChiAll() {
        return dangKyTinChiRepository.findAllDangKyTinChiWithTien();
    }

    public List<HocPhiAdminResponseDTO.DangKyTinChiItem> getDangKyTinChiByHocKi(UUID hocKiId) {
        return dangKyTinChiRepository.findDangKyTinChiByHocKi(hocKiId);
    }

    public List<HocPhiAdminResponseDTO.DangKyTinChiItem> getDangKyTinChiByHocVien(UUID hocVienId) {
        return dangKyTinChiRepository.findDangKyTinChiByHocVien(hocVienId);
    }

    public List<HocPhiAdminResponseDTO.DangKyTinChiTheoHocKi> getDangKyTinChiTongHopTheoHocKi() {
        return dangKyTinChiRepository.findDangKyTinChiTongHopTheoHocKi();
    }

    public List<HocPhiAdminResponseDTO.DangKyTinChiTheoNamHoc> getDangKyTinChiTongHopTheoNamHoc() {
        return dangKyTinChiRepository.findDangKyTinChiTongHopTheoNamHoc();
    }

    public HocPhiAdminResponseDTO.DangKyTinChiTongQuan getDangKyTinChiTongQuan() {
        return dangKyTinChiRepository.findDangKyTinChiTongQuan();
    }

    public Double tinhHocPhiTuTinChi(int soTinChi) {
        return soTinChi * GIA_TIEN_MOT_TIN_CHI;
    }
}
