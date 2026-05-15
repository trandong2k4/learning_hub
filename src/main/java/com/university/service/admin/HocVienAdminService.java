package com.university.service.admin;

import com.alibaba.excel.EasyExcel;
import com.university.dto.request.admin.HocVienAdminRequestDTO;
import com.university.dto.request.admin.HocVienCreateDetailsDTO;
import com.university.dto.request.admin.UsersAdminRequestDTO;
import com.university.dto.request.admin.warrap.HocVienCreateRequestDTO;
import com.university.dto.request.admin.warrap.HocVienFullCreateRequestDTO;
import com.university.mapper.admin.UsersAdminMapper;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.HocVienAdminResponseDTO;
import com.university.dto.response.admin.UsersAdminResponseDTO;
import com.university.entity.HocVien;
import com.university.entity.Nganh;
import com.university.entity.Users;
import com.university.mapper.admin.HocVienAdminMapper;
import com.university.repository.admin.DangKyTinChiAdminRepository;
import com.university.repository.admin.DiemDanhAdminRepository;
import com.university.repository.admin.HocPhiAdminRepository;
import com.university.repository.admin.HocVienAdminRepository;
import com.university.repository.admin.NganhAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import com.university.dto.request.admin.HocVienExcelDTO;
import com.university.service.admin.excel.HocVienExcelListener;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HocVienAdminService {

    private final HocVienAdminRepository hocVienAdminRepository;
    private final UsersAdminRepository usersRepository;
    private final NganhAdminRepository nganhAdminRepository;
    private final DiemDanhAdminRepository diemDanhAdminRepository;
    private final DangKyTinChiAdminRepository dangKyTinChiAdminRepository;
    private final HocPhiAdminRepository hocPhiAdminRepository;
    private final HocVienAdminMapper hocVienAdminMapper;
    private final UsersAdminMapper usersAdminMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ExcelImportResult importFromExcel(MultipartFile file) throws IOException {
        HocVienExcelListener listener = new HocVienExcelListener(
                hocVienAdminRepository,
                usersRepository,
                nganhAdminRepository,
                passwordEncoder);
        EasyExcel.read(file.getInputStream(), HocVienExcelDTO.class, listener)
                .sheet("HocVien")
                .headRowNumber(1)
                .doRead();
        return listener.getResult();
    }

    @Transactional
    public HocVienAdminResponseDTO create(HocVienCreateRequestDTO request) {
        String maHocVien = normalizeCode(request.getMaHocVien());

        if (hocVienAdminRepository.existsByMaHocVien(maHocVien)) {
            throw new IllegalStateException("Mã học viên '" + maHocVien + "' đã tồn tại");
        }

        Nganh nganh = nganhAdminRepository.findByMaNganh(request.getMaNganh().trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Mã ngành '" + request.getMaNganh() + "' không tồn tại"));

        UUID usersId;
        try {
            usersId = UUID.fromString(request.getUsersId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ID tài khoản không hợp lệ");
        }

        Users user = usersRepository.findById(usersId)
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản không tồn tại"));

        if (usersRepository.isUserAlreadyAssigned(usersId)) {
            throw new IllegalStateException(
                    "Tài khoản '" + user.getUsername() + "' đã được gán cho nhân viên hoặc học viên khác");
        }

        HocVien hocVien = new HocVien();
        hocVien.setMaHocVien(maHocVien);
        hocVien.setNganh(nganh);
        hocVien.setNgayNhapHoc(request.getNgayNhapHoc());
        hocVien.setUsers(user);
        hocVien = hocVienAdminRepository.save(hocVien);

        return hocVienAdminMapper.toResponseDTO(hocVien, hocVien.getUsers());
    }

    @Transactional
    public HocVienAdminResponseDTO createFull(HocVienFullCreateRequestDTO request) {
        UsersAdminRequestDTO userDetails = request.getUserDetails();
        HocVienCreateDetailsDTO hocVienDetails = request.getHocVienDetails();

        String maHocVien = normalizeCode(hocVienDetails.getMaHocVien());
        if (hocVienAdminRepository.existsByMaHocVien(maHocVien)) {
            throw new IllegalStateException("Mã học viên '" + maHocVien + "' đã tồn tại");
        }

        Nganh nganh = nganhAdminRepository.findByMaNganh(hocVienDetails.getMaNganh().trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Mã ngành '" + hocVienDetails.getMaNganh() + "' không tồn tại"));

        String userName = userDetails.getUserName() == null ? "" : userDetails.getUserName().trim();
        if (userName.isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }
        if (usersRepository.existsByUserName(userName)) {
            throw new IllegalStateException("Tên đăng nhập '" + userName + "' đã được sử dụng");
        }

        String passWord = userDetails.getPassWord() == null ? "" : userDetails.getPassWord().trim();
        if (passWord.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        String email = userDetails.getEmail() == null ? "" : userDetails.getEmail().trim();
        if (!email.isEmpty() && usersRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email '" + email + "' đã được sử dụng");
        }

        String cccd = userDetails.getCccd() == null ? "" : userDetails.getCccd().trim();
        if (!cccd.isEmpty() && usersRepository.existsByCccd(cccd)) {
            throw new IllegalStateException("CCCD '" + cccd + "' đã được sử dụng");
        }

        Users user = usersAdminMapper.toEntity(userDetails);
        usersRepository.save(user);

        HocVien hocVien = new HocVien();
        hocVien.setMaHocVien(maHocVien);
        hocVien.setNganh(nganh);
        hocVien.setNgayNhapHoc(hocVienDetails.getNgayNhapHoc());
        hocVien.setUsers(user);
        hocVien = hocVienAdminRepository.save(hocVien);

        return hocVienAdminMapper.toResponseDTO(hocVien, hocVien.getUsers());
    }

    @Transactional
    public HocVienAdminResponseDTO assignUser(UUID hocVienId, UUID usersId) {
        HocVien hocVien = hocVienAdminRepository.findById(hocVienId)
                .orElseThrow(() -> new EntityNotFoundException("Học viên không tồn tại"));

        Users user = usersRepository.findById(usersId)
                .orElseThrow(() -> new EntityNotFoundException("Người dùng không tồn tại"));

        if (hocVien.getUsers() != null && hocVien.getUsers().getId().equals(usersId)) {
            throw new IllegalStateException(
                    "Tài khoản '" + user.getUsername() + "' đã được gán cho học viên này rồi");
        }

        if (usersRepository.isUserAlreadyAssigned(usersId)) {
            throw new IllegalStateException(
                    "Tài khoản '" + user.getUsername() + "' đã được gán cho nhân viên hoặc học viên khác");
        }

        hocVien.setUsers(user);
        HocVien saved = hocVienAdminRepository.save(hocVien);
        return hocVienAdminMapper.toResponseDTO(saved, saved.getUsers());
    }

    public List<UsersAdminResponseDTO> getAvailableUsers() {
        return usersRepository.findAllUsersNotAssigned();
    }

    public HocVienAdminResponseDTO getHocVienById(UUID id) {
        HocVien hocVien = hocVienAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Học viên không tồn tại"));
        return hocVienAdminMapper.toResponseDTO(hocVien, hocVien.getUsers());
    }

    public List<HocVienAdminResponseDTO> getAllHocVien() {
        return hocVienAdminRepository.findAllWithDetails();
    }

    @Transactional
    public HocVienAdminResponseDTO updateHocVien(UUID id, HocVienAdminRequestDTO request) {
        HocVien existing = hocVienAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Học viên không tồn tại"));

        String maHocVien = normalizeCode(request.getMaHocVien());
        if (hocVienAdminRepository.existsByMaHocVienAndIdNot(maHocVien, id)) {
            throw new IllegalStateException("Mã học viên '" + maHocVien + "' đã được sử dụng bởi học viên khác");
        }
        request.setMaHocVien(maHocVien);

        Nganh nganh = nganhAdminRepository.findByMaNganh(request.getMaNganh().trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Mã ngành '" + request.getMaNganh() + "' không tồn tại"));

        hocVienAdminMapper.updateEntity(existing, request);
        existing.setNganh(nganh);

        if (existing.getUsers() != null) {
            Users user = existing.getUsers();
            UUID userId = user.getId();

            String newUsername = request.getUsername() == null ? null : request.getUsername().trim();
            if (newUsername != null && !newUsername.isEmpty()) {
                if (usersRepository.existsByUserNameAndIdNot(newUsername, userId)) {
                    throw new IllegalStateException("Tên đăng nhập '" + newUsername + "' đã được sử dụng");
                }
                user.setUserName(newUsername);
            }

            String newEmail = request.getEmail() == null ? null : request.getEmail().trim();
            if (newEmail != null && !newEmail.isEmpty()) {
                if (usersRepository.existsByEmailAndIdNot(newEmail, userId)) {
                    throw new IllegalStateException("Email '" + newEmail + "' đã được sử dụng");
                }
                user.setEmail(newEmail);
            } else if (newEmail != null) {
                user.setEmail(null);
            }

            String newCccd = request.getCccd() == null ? null : request.getCccd().trim();
            if (newCccd != null && !newCccd.isEmpty()) {
                if (usersRepository.existsByCccdAndIdNot(newCccd, userId)) {
                    throw new IllegalStateException("CCCD '" + newCccd + "' đã được sử dụng");
                }
                user.setCccd(newCccd);
            }

            if (request.getHoTen() != null)
                user.setHoTen(request.getHoTen().trim());
            if (request.getPassWord() != null && !request.getPassWord().isBlank()) {
                user.setPassWord(passwordEncoder.encode(request.getPassWord()));
            }
            if (request.getDiaChi() != null)
                user.setDiaChi(request.getDiaChi().trim());
            if (request.getSoDienThoai() != null)
                user.setSoDienThoai(request.getSoDienThoai().trim());
            if (request.getNgaySinh() != null)
                user.setNgaySinh(request.getNgaySinh().atStartOfDay());
            if (request.getGioiTinh() != null)
                user.setGioiTinh(request.getGioiTinh());
            if (request.getTrangThai() != null)
                user.setTrangThai(request.getTrangThai());
            if (request.getGhiChu() != null)
                user.setGhiChu(request.getGhiChu());
            user.setUpdateAt(java.time.LocalDateTime.now());
            usersRepository.save(user);
        }

        HocVien updated = hocVienAdminRepository.save(existing);
        return hocVienAdminMapper.toResponseDTO(updated, updated.getUsers());
    }

    @Transactional
    public void deleteHocVien(UUID id) {
        HocVien hv = hocVienAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Học viên không tồn tại"));

        if (diemDanhAdminRepository.existsByHocVienId(id)) {
            throw new IllegalStateException(
                    "Không thể xóa học viên '" + hv.getMaHocVien() + "' vì đang có dữ liệu điểm danh liên kết");
        }
        if (dangKyTinChiAdminRepository.existsByHocVienId(id)) {
            throw new IllegalStateException(
                    "Không thể xóa học viên '" + hv.getMaHocVien() + "' vì đang có đăng ký tín chỉ liên kết");
        }
        if (hocPhiAdminRepository.existsByHocVienId(id)) {
            throw new IllegalStateException(
                    "Không thể xóa học viên '" + hv.getMaHocVien() + "' vì đang có dữ liệu học phí liên kết");
        }

        if (hv.getUsers() != null) {
            usersRepository.delete(hv.getUsers());
        }
        hocVienAdminRepository.delete(hv);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        for (UUID id : ids) {
            HocVien hv = hocVienAdminRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Học viên không tồn tại (ID: " + id + ")"));

            if (diemDanhAdminRepository.existsByHocVienId(id)) {
                throw new IllegalStateException(
                        "Không thể xóa học viên '" + hv.getMaHocVien() + "' vì đang có dữ liệu điểm danh liên kết");
            }
            if (dangKyTinChiAdminRepository.existsByHocVienId(id)) {
                throw new IllegalStateException(
                        "Không thể xóa học viên '" + hv.getMaHocVien() + "' vì đang có đăng ký tín chỉ liên kết");
            }
            if (hocPhiAdminRepository.existsByHocVienId(id)) {
                throw new IllegalStateException(
                        "Không thể xóa học viên '" + hv.getMaHocVien() + "' vì đang có dữ liệu học phí liên kết");
            }
        }

        for (UUID id : ids) {
            HocVien hv = hocVienAdminRepository.findById(id).orElse(null);
            if (hv != null && hv.getUsers() != null) {
                usersRepository.delete(hv.getUsers());
            }
        }
        hocVienAdminRepository.deleteAllByIdIn(ids);
    }

    private String normalizeCode(String maHocVien) {
        if (maHocVien == null || maHocVien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã học viên không được để trống");
        }
        String normalized = maHocVien.trim().toUpperCase();
        if (normalized.length() > 15) {
            throw new IllegalArgumentException(
                    "Mã học viên tối đa 15 ký tự, hiện tại " + normalized.length() + " ký tự");
        }
        return normalized;
    }
}
