package com.university.service.admin;

import com.alibaba.excel.EasyExcel;
import com.university.dto.request.admin.NhanVienAdminRequestDTO;
import com.university.dto.request.admin.NhanVienCreateDetailsDTO;
import com.university.dto.request.admin.NhanVienExcelDTO;
import com.university.dto.request.admin.NhanVienSimpleRequestDTO;
import com.university.dto.request.admin.UsersAdminRequestDTO;
import com.university.dto.request.admin.warrap.NhanVienCreateRequestDTO;
import com.university.dto.response.admin.ExcelImportResult;
import com.university.dto.response.admin.NhanVienAdminResponseDTO;
import com.university.dto.response.admin.UsersAdminResponseDTO;
import com.university.dto.response.admin.warrap.NhanVienUsersResponseDTO;
import com.university.entity.NhanVien;
import com.university.entity.Users;
import com.university.mapper.admin.NhanVienAdminMapper;
import com.university.mapper.admin.UsersAdminMapper;
import com.university.repository.admin.DanhGiaGiangVienAdminRepository;
import com.university.repository.admin.GiangDayAdminRepository;
import com.university.repository.admin.NhanVienAdminRepository;
import com.university.repository.admin.UsersAdminRepository;
import com.university.service.admin.excel.NhanVienExcelListener;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NhanVienAdminService {

    private final NhanVienAdminRepository nhanVienAdminRepository;
    private final UsersAdminRepository usersRepository;
    private final GiangDayAdminRepository giangDayAdminRepository;
    private final DanhGiaGiangVienAdminRepository danhGiaGiangVienAdminRepository;
    private final NhanVienAdminMapper nhanVienAdminMapper;
    private final UsersAdminMapper usersAdminMapper;
    private final PasswordEncoder passwordEncoder;

    public ExcelImportResult importFromExcel(MultipartFile file) throws java.io.IOException {
        NhanVienExcelListener listener = new NhanVienExcelListener(nhanVienAdminRepository, usersRepository,
                passwordEncoder);

        EasyExcel.read(file.getInputStream(), NhanVienExcelDTO.class, listener)
                .sheet("Nhanvien")
                .headRowNumber(1)
                .doRead();
        return listener.getResult();
    }

    @Transactional
    public NhanVienAdminResponseDTO createSimple(NhanVienSimpleRequestDTO request) {
        String maNhanVien = normalizeMaNhanVien(request.getMaNhanVien());
        if (nhanVienAdminRepository.existsByMaNhanVien(maNhanVien)) {
            throw new IllegalStateException("Mã nhân viên '" + maNhanVien + "' đã tồn tại");
        }

        String usersIdValue = request.getUsersId() == null ? "" : request.getUsersId().trim();
        if (usersIdValue.isEmpty()) {
            throw new IllegalArgumentException("Tài khoản không được để trống");
        }

        UUID usersId;
        try {
            usersId = UUID.fromString(usersIdValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ID tài khoản không hợp lệ");
        }

        Users user = usersRepository.findById(usersId)
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản không tồn tại"));

        if (usersRepository.isUserAlreadyAssigned(usersId)) {
            throw new IllegalStateException(
                    "Tài khoản '" + user.getUsername() + "' đã được gán cho nhân viên hoặc học viên khác");
        }

        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNhanVien(maNhanVien);
        nhanVien.setNgayNhanViec(request.getNgayNhanViec());
        nhanVien.setNgayNghiViec(request.getNgayNghiViec());
        nhanVien.setUsers(user);

        NhanVien saved = nhanVienAdminRepository.save(nhanVien);
        return nhanVienAdminMapper.toResponseDTO(saved, saved.getUsers());
    }

    @Transactional
    public NhanVienUsersResponseDTO createDTO(NhanVienCreateRequestDTO request) {
        UsersAdminRequestDTO userDetails = request.getUserDetails();
        NhanVienCreateDetailsDTO nhanVienDetails = request.getNhanVienDetails();

        String maNhanVien = normalizeMaNhanVien(nhanVienDetails.getMaNhanVien());
        if (nhanVienAdminRepository.existsByMaNhanVien(maNhanVien)) {
            throw new IllegalStateException("Mã nhân viên '" + maNhanVien + "' đã tồn tại");
        }

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

        Users users = usersAdminMapper.toEntity(userDetails);
        usersRepository.save(users);

        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNhanVien(maNhanVien);
        nhanVien.setNgayNhanViec(nhanVienDetails.getNgayNhanViec());
        nhanVien.setNgayNghiViec(nhanVienDetails.getNgayNghiViec());
        nhanVien.setUsers(users);
        nhanVienAdminRepository.save(nhanVien);

        NhanVienUsersResponseDTO response = new NhanVienUsersResponseDTO();
        response.setUserDetails(usersAdminMapper.toResponseDTO(users));
        response.setNhanVienDetails(nhanVienAdminMapper.toResponseDTO(nhanVien, users));
        return response;
    }

    @Transactional
    public NhanVienAdminResponseDTO assignUser(UUID nhanVienId, UUID usersId) {
        NhanVien nhanVien = nhanVienAdminRepository.findById(nhanVienId)
                .orElseThrow(() -> new EntityNotFoundException("Nhân viên không tồn tại"));

        Users user = usersRepository.findById(usersId)
                .orElseThrow(() -> new EntityNotFoundException("Người dùng không tồn tại"));

        if (nhanVien.getUsers() != null && nhanVien.getUsers().getId().equals(usersId)) {
            throw new IllegalStateException(
                    "Tài khoản '" + user.getUsername() + "' đã được gán cho nhân viên này rồi");
        }

        if (usersRepository.isUserAlreadyAssigned(usersId)) {
            throw new IllegalStateException(
                    "Tài khoản '" + user.getUsername() + "' đã được gán cho nhân viên hoặc học viên khác");
        }

        nhanVien.setUsers(user);
        NhanVien saved = nhanVienAdminRepository.save(nhanVien);
        return nhanVienAdminMapper.toResponseDTO(saved, saved.getUsers());
    }

    public List<UsersAdminResponseDTO> getAvailableUsers() {
        return usersRepository.findAllUsersNotAssigned();
    }

    @Transactional(readOnly = true)
    public NhanVienAdminResponseDTO getNhanVienById(UUID id) {
        return nhanVienAdminRepository.findStaffDTOById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nhân viên không tồn tại"));
    }

    @Transactional(readOnly = true)
    public List<NhanVienAdminResponseDTO> getAll() {
        return nhanVienAdminRepository.findAllStaffDTO();
    }

    @Transactional(readOnly = true)
    public List<NhanVienAdminResponseDTO> getAllLecturers() {
        return nhanVienAdminRepository.findAllLecturersDTO();
    }

    @Transactional
    public NhanVienAdminResponseDTO update(UUID id, NhanVienAdminRequestDTO request) {
        NhanVien existing = nhanVienAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nhân viên không tồn tại"));

        String maNhanVien = normalizeMaNhanVien(request.getMaNhanVien());
        if (nhanVienAdminRepository.existsByMaNhanVienAndIdNot(maNhanVien, id)) {
            throw new IllegalStateException("Mã nhân viên '" + maNhanVien + "' đã được sử dụng bởi nhân viên khác");
        }
        request.setMaNhanVien(maNhanVien);

        if (request.getNgayNhanViec() != null && request.getNgayNghiViec() != null
                && request.getNgayNghiViec().isBefore(request.getNgayNhanViec())) {
            throw new IllegalArgumentException("Ngày nghỉ việc phải sau ngày nhận việc");
        }

        nhanVienAdminMapper.updateEntity(existing, request);

        // Cập nhật thông tin tài khoản người dùng liên kết
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

            if (request.getHoTen() != null) user.setHoTen(request.getHoTen().trim());
            if (request.getPassWord() != null && !request.getPassWord().isBlank()) {
                user.setPassWord(passwordEncoder.encode(request.getPassWord()));
            }
            if (request.getDiaChi() != null) user.setDiaChi(request.getDiaChi().trim());
            if (request.getSoDienThoai() != null) user.setSoDienThoai(request.getSoDienThoai().trim());
            if (request.getNgaySinh() != null) user.setNgaySinh(request.getNgaySinh().atStartOfDay());
            if (request.getGioiTinh() != null) user.setGioiTinh(request.getGioiTinh());
            if (request.getTrangThai() != null) user.setTrangThai(request.getTrangThai());
            if (request.getGhiChu() != null) user.setGhiChu(request.getGhiChu());
            user.setUpdateAt(java.time.LocalDateTime.now());
            usersRepository.save(user);
        }

        NhanVien updated = nhanVienAdminRepository.save(existing);
        return nhanVienAdminMapper.toResponseDTO(updated, updated.getUsers());
    }

    @Transactional
    public void deleteNhanVien(UUID id) {
        NhanVien nv = nhanVienAdminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nhân viên không tồn tại"));

        if (giangDayAdminRepository.existsByNhanVienId(id)) {
            throw new IllegalStateException(
                    "Không thể xóa nhân viên '" + nv.getMaNhanVien() + "' vì đang có lớp giảng dạy liên kết");
        }
        if (danhGiaGiangVienAdminRepository.existsByNhanVienId(id)) {
            throw new IllegalStateException(
                    "Không thể xóa nhân viên '" + nv.getMaNhanVien() + "' vì đang có đánh giá giảng viên liên kết");
        }

        if (nv.getUsers() != null) {
            usersRepository.delete(nv.getUsers());
        }
        nhanVienAdminRepository.delete(nv);
    }

    @Transactional
    public void deleteAllByList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        for (UUID id : ids) {
            NhanVien nv = nhanVienAdminRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Nhân viên không tồn tại (ID: " + id + ")"));

            if (giangDayAdminRepository.existsByNhanVienId(id)) {
                throw new IllegalStateException(
                        "Không thể xóa nhân viên '" + nv.getMaNhanVien() + "' vì đang có lớp giảng dạy liên kết");
            }
            if (danhGiaGiangVienAdminRepository.existsByNhanVienId(id)) {
                throw new IllegalStateException(
                        "Không thể xóa nhân viên '" + nv.getMaNhanVien() + "' vì đang có đánh giá giảng viên liên kết");
            }
        }

        for (UUID id : ids) {
            NhanVien nv = nhanVienAdminRepository.findById(id).orElse(null);
            if (nv != null && nv.getUsers() != null) {
                usersRepository.delete(nv.getUsers());
            }
        }
        nhanVienAdminRepository.deleteAllByIdIn(ids);
    }

    private String normalizeMaNhanVien(String maNhanVien) {
        if (maNhanVien == null || maNhanVien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống");
        }

        String normalized = maNhanVien.trim().toUpperCase();
        if (normalized.length() > 10) {
            throw new IllegalArgumentException(
                    "Mã nhân viên tối đa 10 ký tự, hiện tại " + normalized.length() + " ký tự");
        }

        return normalized;
    }
}
